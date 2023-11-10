package com.planetscale.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.naming.Named;
import io.micronaut.core.util.Toggleable;
import io.micronaut.jdbc.BasicJdbcConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * TBD.
 */
@EachProperty(value = PlanetscaleModuleConfiguration.PREFIX + ".databases", primary = "default")
public final class PlanetscaleDatabaseConfiguration implements
        Named,
        Toggleable,
        PlanetscaleDatabaseProfile,
        Comparable<PlanetscaleDatabaseConfiguration> {
    private static final String DRIVER_FACADE_CLASS_NAME = "com.planetscale.Driver";
    private static final String DEFAULT_VALIDATION_QUERY = "SELECT 1;";

    /**
     * TBD.
     */
    @ConfigurationProperties("features")
    public static class PlanetscaleDatabaseFeatureConfiguration implements PlanetscaleDatabaseFeatures {
        private boolean enableBoost = false;

        public Boolean getBoost() {
            return enableBoost;
        }

        public void setBoost(Boolean enableBoost) {
            this.enableBoost = enableBoost;
        }
    }

    private final String name;
    private String db;
    private String username;
    private String password;
    private Set<String> endpoints;
    private PlanetscaleDatabaseFeatureConfiguration features = new PlanetscaleDatabaseFeatureConfiguration();

    public PlanetscaleDatabaseConfiguration(@Parameter String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDb() {
        return this.db;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Set<String> getEndpoints() {
        return this.endpoints;
    }

    @Override
    public PlanetscaleDatabaseFeatureConfiguration getFeatures() {
        return this.features;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEndpoints(Set<String> endpoints) {
        this.endpoints = endpoints;
    }

    public void setFeatures(PlanetscaleDatabaseFeatureConfiguration features) {
        this.features = features;
    }

    @Override
    public int compareTo(@NotNull PlanetscaleDatabaseConfiguration o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanetscaleDatabaseConfiguration that = (PlanetscaleDatabaseConfiguration) o;
        return Objects.equals(name, that.name) && Objects.equals(db, that.db) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(endpoints, that.endpoints) && Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, db, username, password, endpoints, features);
    }

    BasicJdbcConfiguration build() {
        var that = this;
        var endpointJoiner = new StringJoiner("..");
        getEndpoints().forEach(endpointJoiner::add);
        var endpoints =  endpointJoiner.toString();
        String connectionString = "jdbc:planetscale://" +
                getUsername() +
                ":" +
                getPassword() +
                "@" +
                endpoints +
                "/" +
                getDb();
        AtomicReference<String> activeDriver = new AtomicReference<>(DRIVER_FACADE_CLASS_NAME);
        AtomicReference<String> activeUsername = new AtomicReference<>(getUsername());
        AtomicReference<String> activePassword = new AtomicReference<>(getPassword());
        AtomicReference<String> activeUrl = new AtomicReference<>(connectionString);
        AtomicReference<Map<String, ?>> activeProps = new AtomicReference<>();
        return new BasicJdbcConfiguration() {
            @Override
            public String getName() {
                return that.getName();
            }

            @Override
            public String getConfiguredUrl() {
                return activeUrl.get();
            }

            @Override
            public String getUrl() {
                return connectionString;
            }

            @Override
            public void setUrl(String url) {
                activeUrl.set(url);
            }

            @Override
            public String getConfiguredDriverClassName() {
                return activeDriver.get();
            }

            @Override
            public String getDriverClassName() {
                return DRIVER_FACADE_CLASS_NAME;
            }

            @Override
            public void setDriverClassName(String driverClassName) {
                activeDriver.set(driverClassName);
            }

            @Override
            public String getConfiguredUsername() {
                return activeUsername.get();
            }

            @Override
            public String getUsername() {
                return that.getUsername();
            }

            @Override
            public void setUsername(String username) {
                activeUsername.set(username);
            }

            @Override
            public String getConfiguredPassword() {
                return activePassword.get();
            }

            @Override
            public String getPassword() {
                return activePassword.get();
            }

            @Override
            public void setPassword(String password) {
                activePassword.set(password);
            }

            @Override
            public String getConfiguredValidationQuery() {
                return DEFAULT_VALIDATION_QUERY;
            }

            @Override
            public String getValidationQuery() {
                return DEFAULT_VALIDATION_QUERY;
            }

            @Override
            public void setDataSourceProperties(Map<String, ?> dsProperties) {
                activeProps.set(dsProperties);
            }
        };
    }
}
