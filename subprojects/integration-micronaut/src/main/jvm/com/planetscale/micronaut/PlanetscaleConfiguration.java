package com.planetscale.micronaut;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.Toggleable;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Requires(bean = PlanetscaleModuleConfiguration.class)
public final class PlanetscaleConfiguration implements Toggleable, Comparable<PlanetscaleConfiguration> {
    private final PlanetscaleModuleConfiguration moduleConfiguration;
    private final Set<PlanetscaleDatabaseConfiguration> profiles;
    private final Map<String, PlanetscaleDatabaseConfiguration> profileMap;

    PlanetscaleConfiguration(PlanetscaleModuleConfiguration moduleConfiguration, Collection<PlanetscaleDatabaseConfiguration> profiles) {
        this.moduleConfiguration = moduleConfiguration;
        this.profiles = new TreeSet<>(profiles);
        this.profileMap = profiles.stream().collect(Collectors.toMap(PlanetscaleDatabaseConfiguration::getName, Function.identity()));
    }

    @Override
    public boolean isEnabled() {
        return moduleConfiguration.isEnabled();
    }

    public PlanetscaleModuleConfiguration getModuleConfiguration() {
        return moduleConfiguration;
    }

    public Set<PlanetscaleDatabaseConfiguration> getProfiles() {
        return profiles;
    }

    public Optional<PlanetscaleDatabaseConfiguration> getProfile(String name) {
        return Optional.ofNullable(profileMap.get(name));
    }

    @Override
    public int compareTo(@NotNull PlanetscaleConfiguration o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanetscaleConfiguration that = (PlanetscaleConfiguration) o;
        return Objects.equals(moduleConfiguration, that.moduleConfiguration) && Objects.equals(profiles, that.profiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleConfiguration, profiles);
    }

    @Factory
    public static class PlanetscaleConfigurationFactory {
        @Singleton
        PlanetscaleConfiguration planetscaleConfiguration(PlanetscaleModuleConfiguration moduleConfiguration, Collection<PlanetscaleDatabaseConfiguration> profiles) {
            return new PlanetscaleConfiguration(moduleConfiguration, profiles);
        }
    }
}
