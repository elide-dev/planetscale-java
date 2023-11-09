package com.planetscale.jvm.mysqlj

import com.planetscale.jvm.PlanetscaleConfig
import com.planetscale.jvm.PlanetscaleCredential
import com.planetscale.jvm.PlanetscaleParameter
import com.planetscale.jvm.driver.AbstractPlanetscaleAdapter
import com.planetscale.jvm.driver.CloudProvider
import com.planetscale.jvm.driver.Constants
import java.net.URI
import java.sql.Driver
import java.sql.DriverManager

/**
 * TBD.
 */
public class PlanetscaleMysqlDriver : AbstractPlanetscaleAdapter() {
    public companion object {
        private val defaultParams = mapOf(
            Constants.DriverParams.AUTO_RECONNECT to Constants.StringValue.TRUE,
            Constants.DriverParams.CACHE_SERVER_CONFIG to Constants.StringValue.TRUE,
            Constants.DriverParams.CACHE_RESULT_SET_METADATA to Constants.StringValue.TRUE,
            Constants.DriverParams.ROUND_ROBIN_LOAD_BALANCE to Constants.StringValue.TRUE,
            Constants.DriverParams.SSL_MODE to Constants.DriverParams.SSL_MODE_VERIFY,
        )

        // Return a qualified Planetscale endpoint.
        @JvmStatic public fun qualifiedEndpoint(qualifier: String): String = "$qualifier.${Constants.CONNECT_DOMAIN}"

        // Return a qualified Planetscale endpoint for AWS.
        @JvmStatic public fun awsEndpoint(qualifier: String? = null): String = when (qualifier?.ifBlank { null }) {
            null -> qualifiedEndpoint(Constants.Provider.AWS)
            else -> qualifiedEndpoint("$qualifier.${Constants.Provider.AWS}")
        }

        // Return a qualified Planetscale endpoint for GCP.
        @JvmStatic public fun gcpEndpoint(qualifier: String? = null): String = when (qualifier?.ifBlank { null }) {
            null -> qualifiedEndpoint(Constants.Provider.GCP)
            else -> qualifiedEndpoint("$qualifier.${Constants.Provider.GCP}")
        }

        // Resolve the active cloud provider, if any.
        @JvmStatic public fun detectProvider(): CloudProvider? {
            return listOf(
                Constants.Provider.GCP_CREDS_VAR to CloudProvider.GCP,
                Constants.Provider.AWS_CREDS_VAR to CloudProvider.AWS,
            ).firstOrNull {
                System.getenv(it.first) != null
            }?.second
        }

        // Resolve symbolic host names to their actual targets.
        @JvmStatic public fun resolveHostSymbols(target: String): String {
            // safeguard: if it contains more than one period, it's a hostname
            return if (target.count { it == '.' } > 1) {
                target
            } else {
                when (target) {
                    // covers literal `aws`
                    Constants.SymbolicHosts.AWS -> awsEndpoint()

                    // covers literal `gcp`
                    Constants.SymbolicHosts.GCP -> gcpEndpoint()

                    else -> when {
                        // covers `<region>.aws`
                        target.endsWith(Constants.SymbolicHosts.AWS) -> awsEndpoint(
                            target.removeSuffix(Constants.SymbolicHosts.AWS).dropLast(1),
                        )

                        // covers `<region>.gcp`
                        target.endsWith(Constants.SymbolicHosts.GCP) -> gcpEndpoint(
                            target.removeSuffix(Constants.SymbolicHosts.GCP).dropLast(1),
                        )

                        // otherwise, we don't recognize it, and it should be preserved
                        else -> target
                    }
                }
            }
        }
    }

    private fun PlanetscaleCredential.toUriInfo(): StringBuilder = StringBuilder().apply {
        append(username)
        append(":")
        append(password)
    }

    private fun StringBuilder.appendHost(host: String) {
        append(host)
        append(":")
        append(Constants.MYSQL_PORT)
    }

    private fun PlanetscaleConfig.endpoints(): StringBuilder = StringBuilder().apply {
        if (multiHost()) {
            append(
                targetHosts().map {
                    StringBuilder().appendHost(resolveHostSymbols(it))
                }.joinToString(","),
            )
        } else {
            appendHost(resolveHostSymbols(targetHosts().first()))
        }
    }

    private fun PlanetscaleConfig.sessionVariables(): List<Pair<String, String>>? {
        return if (!enableBoost()) {
            null
        } else {
            listOf(
                PlanetscaleParameter.EnableBoost.option.paramName to Constants.StringValue.TRUE,
            )
        }
    }

    private fun PlanetscaleConfig.driverParameters(): List<Pair<String, String>> {
        return defaultParams.plus(
            extraParams().mapNotNull {
                if (it.value.isNullOrEmpty() || it.value.isBlank()) {
                    null
                } else {
                    it.key to it.value
                }
            },
        ).toSortedMap().toList().plus(
            when (val sessionVariables = sessionVariables()) {
                null -> emptyList()
                else -> listOf(
                    Constants.DriverParams.SESSION_VARIABLES to
                        sessionVariables.joinToString(separator = ",") { (k, v) -> "$k=$v" },
                )
            },
        )
    }

    override fun PlanetscaleConfig.toURI(): URI {
        return URI.create(
            StringBuilder().apply {
                append(Constants.Prefix.MYSQL)
                if (multiHost()) append(Constants.Prefix.REPLICATION)
                append("//")
                credential?.let { append(it.toUriInfo()) }
                append("@")
                append(endpoints())
                append("/")
                credential?.let { append(it.database) }
                append("?")
                driverParameters().joinToString("&") { (k, v) ->
                    append(k)
                    append("=")
                    append(v)
                }
            }.toString(),
        )
    }

    override fun createDriver(): Driver = DriverManager.drivers().filter {
        it.javaClass.canonicalName == Constants.MYSQL_DRIVER
    }.findFirst().orElse(null) ?: error(
        "Failed to resolve MySQL driver: check your classpath?",
    )
}
