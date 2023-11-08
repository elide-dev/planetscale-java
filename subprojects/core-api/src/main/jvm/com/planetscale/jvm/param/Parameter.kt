package com.planetscale.jvm.param

import com.planetscale.jvm.DriverParameter
import java.net.URI
import java.util.Properties

/**
 * # Driver Parameter
 *
 */
public sealed interface Parameter<V> {
    /**
     * TBD.
     */
    public val name: ParameterName

    /**
     * TBD.
     */
    public val value: V?

    /** Models the concept of a literal value as a driver parameter. */
    @JvmInline public value class LiteralParameter<V>(private val pair: Pair<ParameterName, V?>) : Parameter<V> {
        override val name: ParameterName get() = pair.first
        override val value: V? get() = pair.second
    }

    /**
     * TBD.
     */
    @FunctionalInterface public fun interface ParameterFactory<V> {
        /**
         * TBD.
         */
        public fun parse(optionValue: String): V?
    }

    /**
     * TBD.
     */
    public sealed interface ParameterParser<V>: ParameterFactory<V> {
        /** @return Driver parameter controlled by this namer. */
        public val option: DriverParameter

        /** @return URI parameter name for this option. */
        public fun uriName(): String = option.uriParam

        /** @return Environment variable name for this parser. */
        public fun envName(): String? = option.envVar

        /** @return VM property name for this parameter. */
        public fun vmPropName(): String = option.vmProp

        /** @return Resolved value of this parameter, if available, or `null`. */
        public fun resolve(uri: URI, params: Map<String, String>, properties: Properties?): V? {
            return params[uriName()]?.let { parse(it) }
                ?: envName()?.let { System.getenv(it) }?.let { parse(it) }
                ?: properties?.getProperty(vmPropName())?.let { parse(it) }
                ?: System.getProperty(vmPropName())?.let { parse(it) }
        }

        /** @return Resolved value of this parameter, if available, or `null`. */
        public fun resolveOr(uri: URI, params: Map<String, String>, properties: Properties?, default: V): V {
            return resolve(uri, params, properties) ?: default
        }
    }

    /** TBD. */
    public abstract class BooleanOptionFactory (override val option: DriverParameter): ParameterParser<Boolean> {
        override fun parse(optionValue: String): Boolean? = optionValue.toBooleanStrictOrNull()
    }

    /** TBD. */
    public abstract class StringOptionFactory (override val option: DriverParameter): ParameterParser<String> {
        override fun parse(optionValue: String): String? = optionValue.let { it.ifBlank { null } }
    }

    public companion object {
        /**
         * TBD.
         */
        @JvmStatic public fun <V> literal(name: ParameterName, value: V?): Parameter<V> =
            LiteralParameter(name to value)
    }
}
