package com.planetscale.jvm

import com.planetscale.jvm.driver.Constants
import java.net.URI

/**
 * # Planetscale Credential
 */
public sealed interface PlanetscaleCredential {
    /**
     * TBD.
     */
    public val database: String

    /**
     * TBD.
     */
    public val username: String

    /**
     * TBD.
     */
    public val password: String

    /** TBD. */
    public companion object {
        private fun dbNameFromUri(uri: URI): String {
            return uri.toString()
                .drop("${Constants.Prefix.PLANETSCALE}//".length)
                .substringAfter("/")
                .substringBefore("?")
                .trim()
        }

        /** Lightweight wrapper for a validated Planetscale credential. */
        @JvmInline internal value class PlanetscaleUser(
            private val credential: PlanetscaleCredentialInfo,
        ) : PlanetscaleCredential {
            companion object {
                @JvmStatic internal fun of(db: String, user: String, pass: String): PlanetscaleCredential {
                    return PlanetscaleUser(
                        PlanetscaleCredentialInfo(
                            database = db,
                            username = user,
                            password = pass,
                        ),
                    )
                }
            }

            override val database: String get() = credential.database
            override val username: String get() = credential.username
            override val password: String get() = credential.password
        }

        /**
         * TBD.
         */
        @JvmStatic public fun user(db: String, username: String, password: String): PlanetscaleCredential =
            PlanetscaleUser.of(db, username, password)

        /**
         * TBD.
         */
        @JvmStatic public fun resolve(uri: URI): PlanetscaleCredential? {
            val userInfo = uri.toString()
                .substringAfter("://")
                .substringBefore("@")
                .trim()

            return userInfo.ifBlank { null }?.let {
                when {
                    it.contains(":") -> it.split(":").let { split ->
                        if (split.size == 2) {
                            val user = split.first().ifEmpty { null }
                            val pass = split[1].ifEmpty { null }
                            if (user.isNullOrBlank() || pass.isNullOrBlank()) {
                                (null to null)
                            } else {
                                user to pass
                            }
                        } else {
                            error("Invalid user/password specification in database URI")
                        }
                    }
                    else -> it to null
                }.let { (user, pass) ->
                    (
                        user ?: System.getProperty(Constants.USER_VM_PROPERTY, System.getenv(Constants.USER_ENV_VAR))
                        ) to (
                        pass ?: System.getProperty(Constants.PASS_VM_PROPERTY, System.getenv(Constants.PASS_ENV_VAR))
                        )
                }
            }?.let { (user, pass) ->
                PlanetscaleUser.of(dbNameFromUri(uri), user, pass)
            }
        }
    }
}
