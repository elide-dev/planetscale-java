package com.planetscale.micronaut;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.jdbc.BasicJdbcConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * TBD.
 */
@Factory
@Requires(bean = PlanetscaleModuleConfiguration.class)
@Requires(property = PlanetscaleModuleConfiguration.PREFIX + ".enabled", notEquals = "false")
public final class PlanetscaleConfigurationFactory {
    private final static PlanetscaleConfigurationFactory INSTANCE = new PlanetscaleConfigurationFactory();

    /**
     * TBD.
     *
     * @param subject
     * @return
     */
    public static @NotNull BasicJdbcConfiguration buildFrom(@NotNull PlanetscaleDatabaseConfiguration subject) {
        return INSTANCE.buildPlanetscaleJdbcConfiguration(subject).orElseThrow();
    }

    @NotNull
    @EachBean(PlanetscaleDatabaseConfiguration.class)
    Optional<BasicJdbcConfiguration> buildPlanetscaleJdbcConfiguration(PlanetscaleDatabaseConfiguration subject) {
        return Optional.of(subject.build());
    }
}
