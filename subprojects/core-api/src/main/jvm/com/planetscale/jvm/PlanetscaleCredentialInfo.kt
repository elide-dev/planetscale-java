package com.planetscale.jvm

/**
 * TBD.
 */
@JvmRecord internal data class PlanetscaleCredentialInfo(
    val database: String,
    val username: String,
    val password: String,
)
