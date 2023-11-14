package com.planetscale.jvm

import java.sql.Connection

/** Planetscale wrapped and managed connection. */
public class PlanetscaleCloudConnection private constructor(
    override val adapter: PlanetscaleAdapter,
    private val connection: Connection,
) : PlanetscaleConnection, Connection by connection {
    public companion object {
        @JvmStatic
        public fun create(adapter: PlanetscaleAdapter, connection: Connection): PlanetscaleCloudConnection {
            return PlanetscaleCloudConnection(adapter, connection)
        }
    }
}
