package com.planetscale.jvm.param

/**
 * TBD.
 */
public enum class ParameterSource {
    /** The value originates from the driver URL. */
    URL,

    /** The value originates from environment variables. */
    ENVIRONMENT,

    /** The value originates from VM properties. */
    VM_PROPERTY,

    /** The value is a default or provided programmatically. */
    PROGRAMMATIC,
}
