package com.planetscale.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.Toggleable;

/**
 * TBD.
 */
@ConfigurationProperties(PlanetscaleModuleConfiguration.PREFIX)
public final class PlanetscaleModuleConfiguration implements Toggleable {
    /**
     * The prefix used for Planetscale configuration.
     */
    public static final String PREFIX = "planetscale";

    private boolean debug;
    private boolean enabled;

    public PlanetscaleModuleConfiguration() {
        this.debug = false;
        this.enabled = true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Retrieve debug state for the Planetscale adapter and integration.
     *
     * @return Debug state; defaults to `false`.
     */
    boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
