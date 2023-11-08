package com.planetscale.jvm

/**
 * TBD.
 */
public enum class DriverParameter (
    public val paramName: String,
    internal val uriParam: String,
    internal val vmProp: String,
    internal val envVar: String? = null,
) {
    /**
     * TBD.
     */
    ENABLE_BOOST(
        "boost_cached_queries",
        "enableBoost",
        "planetscale.enableBoost",
        "PLANETSCALE_ENABLE_BOOST",
    ),
}
