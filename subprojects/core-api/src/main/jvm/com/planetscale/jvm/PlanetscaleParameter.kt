package com.planetscale.jvm

import com.planetscale.jvm.param.Parameter
import com.planetscale.jvm.param.ParameterName
import com.planetscale.jvm.param.ParameterSource
import java.util.SortedSet
import kotlin.reflect.KClass

/**
 * # Planetscale Parameter
 */
public sealed interface PlanetscaleParameter<Type> {
    /**
     * TBD.
     */
    public val key: DriverParameter

    /**
     * TBD.
     */
    public val value: Type

    /**
     * TBD.
     */
    public val param: Parameter<Type>

    /**
     * TBD.
     */
    public val source: ParameterSource

    public companion object {
        // All configured driver options.
        private val allDriverOptions: List<Parameter.ParameterParser<*>> = listOf(
            EnableBoost.Companion,
        )

        // All parameter names that are specific to this meta-driver.
        internal val allKnownParamNames: SortedSet<String> = allDriverOptions.map { it.option.paramName }.toSortedSet()
    }

    /**
     * Parameter: `enableBoost`.
     */
    @JvmInline public value class EnableBoost(private val status: Pair<ParameterSource, Boolean>) :
        PlanetscaleParameter<Boolean> {
        override val key: DriverParameter get() = DriverParameter.ENABLE_BOOST
        override val param: Parameter<Boolean> get() = Parameter.literal(key.name, status.second)
        override val source: ParameterSource get() = status.first
        override val value: Boolean get() = status.second

        public companion object: Parameter.BooleanOptionFactory(DriverParameter.ENABLE_BOOST) {
            /** @return "Enable Boost" setting with the provided [enabled] status, and optional [source]. */
            @JvmStatic public fun of(
                enabled: Boolean,
                source: ParameterSource = ParameterSource.PROGRAMMATIC,
            ): EnableBoost = EnableBoost(source to enabled)

            /** @return Enabled Boost setting, with optional [source]. */
            @JvmStatic public fun enabled(source: ParameterSource = ParameterSource.PROGRAMMATIC): EnableBoost =
                of(true, source)

            /** @return Disabled Boost setting, with optional [source]. */
            @JvmStatic public fun disabled(source: ParameterSource = ParameterSource.PROGRAMMATIC): EnableBoost =
                of(false, source)
        }
    }
}
