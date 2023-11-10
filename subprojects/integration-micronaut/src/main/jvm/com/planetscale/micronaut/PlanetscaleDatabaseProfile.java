package com.planetscale.micronaut;

import java.util.Set;

/**
 * TBD.
 */
public interface PlanetscaleDatabaseProfile {
    /**
     * The prefix used for Planetscale database configurations.
     */
    String PREFIX = "planetscale.databases";

    /**
     * Retrieve the configured Planetscale database name.
     *
     * @return Database name.
     */
    String getDb();

    /**
     * Retrieve the configured Planetscale username.
     *
     * @return Username.
     */
    String getUsername();

    /**
     * Retrieve the configured Planetscale password.
     *
     * @return Password.
     */
    String getPassword();

    /**
     * Retrieve the configured Planetscale endpoints.
     *
     * @return Planetscale endpoints.
     */
    Set<String> getEndpoints();

    /**
     * Retrieve the configured Planetscale features.
     *
     * @return Planetscale features.
     */
    PlanetscaleDatabaseFeatures getFeatures();
}
