package com.planetscale.jvm

import java.sql.Connection

/**
 * # Planetscale Connection
 */
public interface PlanetscaleConnection: Connection {
    /**
     * TBD.
     */
    public val adapter: PlanetscaleAdapter
}
