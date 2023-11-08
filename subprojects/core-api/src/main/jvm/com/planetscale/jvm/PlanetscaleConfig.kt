@file:Suppress("DataClassPrivateConstructor")

package com.planetscale.jvm

import com.planetscale.jvm.param.Defaults
import com.planetscale.jvm.param.ParameterSource
import com.planetscale.jvm.PlanetscaleParameter.EnableBoost
import com.planetscale.jvm.driver.Constants
import java.net.URI
import java.util.Properties
import java.util.SortedMap
import java.util.TreeMap

/**
 * TBD.
 */
public interface PlanetscaleConfig {
    public companion object {
        /**
         * TBD.
         */
        @JvmRecord public data class PlanetscaleConfigState internal constructor (
            override val uri: URI,
            override val credential: PlanetscaleCredential,
            val hosts: List<String>,
            val enableBoost: EnableBoost,
            val extra: SortedMap<String, String>,
        ) : PlanetscaleConfig {
            override val state: PlanetscaleConfigState get() = this
        }

        /**
         * TBD.
         */
        private fun extractParams(uri: URI): Map<String, String> {
            return uri.query
                ?.split("&")
                ?.map { it.split("=") }
                ?.associate { it[0] to it[1] }
                ?: emptyMap()
        }

        /**
         * TBD.
         */
        private fun extractHosts(uri: URI): List<String> {
            return uri.toString()
                .substringAfter("@")
                .substringBefore("/")
                .trim()
                .split(Constants.UrlTokens.HOST_SEPARATOR)
                .map { it.trim() }
        }

        /**
         * TBD.
         */
        private fun extraParamsOnly(map: Map<String, String>): SortedMap<String, String> {
            return map.filterKeys { !PlanetscaleParameter.allKnownParamNames.contains(it) }.toSortedMap()
        }

        /**
         * TBD.
         */
        @JvmStatic public fun parseUri(uri: String): URI {
            return if (!uri.startsWith("jdbc:")) error("Cannot parse non-JDBC URIs") else URI.create(
                // should be `planetscale://...`
                uri.drop("jdbc:".length)
            )
        }

        /**
         * TBD.
         */
        @JvmStatic public fun of(
            uri: URI,
            credential: PlanetscaleCredential,
            source: ParameterSource = ParameterSource.PROGRAMMATIC,
            enableBoost: Boolean = Defaults.enableBoost,
            extra: SortedMap<String, String> = TreeMap(),
        ): PlanetscaleConfig = PlanetscaleConfigState(
            uri,
            credential,
            hosts = extractHosts(uri),
            enableBoost = EnableBoost.of(enableBoost, source),
            extra = extra,
        )

        /** @return Set of default configurations for the provided [credential]. */
        @JvmStatic public fun defaults(uri: URI, credential: PlanetscaleCredential): PlanetscaleConfig =
            extractParams(uri).let { params ->
                PlanetscaleConfigState(
                    uri,
                    credential,
                    hosts = extractHosts(uri),
                    extra = extraParamsOnly(params),
                    enableBoost = EnableBoost.of(
                        EnableBoost.resolveOr(uri, params, null, Defaults.enableBoost)
                    ),
                )
            }

        /** @return Configuration resolved from the provided [uri] and environment or VM property inputs. */
        @JvmStatic public fun resolve(
            params: Map<String, String>,
            properties: Properties?,
            uri: URI,
        ): PlanetscaleConfig = PlanetscaleConfigState(
            uri,
            requireNotNull(PlanetscaleCredential.resolve(uri)) { "No Planetscale credential found" },
            hosts = extractHosts(uri),
            enableBoost = EnableBoost.of(EnableBoost.resolveOr(uri, params, properties, Defaults.enableBoost)),
            extra = extraParamsOnly(params),
        )

        /** @return Configuration resolved from provided args and environment or VM property inputs. */
        @JvmStatic public fun resolve(uri: URI, properties: Properties? = null): PlanetscaleConfig = resolve(
            extractParams(uri),
            properties,
            uri,
        )
    }

    /**
     * TBD.
     */
    public val state: PlanetscaleConfigState

    /**
     * TBD.
     */
    public val uri: URI

    /**
     * TBD.
     */
    public val credential: PlanetscaleCredential

    /**
     * TBD.
     */
    public fun enableBoost(): Boolean = state.enableBoost.value

    /**
     * TBD.
     */
    public fun multiHost(): Boolean = state.hosts.size > 1

    /**
     * TBD.
     */
    public fun targetHosts(): List<String> = state.hosts

    /**
     * TBD.
     */
    public fun extraParams(): SortedMap<String, String> = state.extra
}
