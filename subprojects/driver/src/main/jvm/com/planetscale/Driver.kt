package com.planetscale

import com.planetscale.jvm.driver.PlanetscaleDriver
import java.sql.DriverManager
import java.sql.SQLException

/**
 * # Planetscale Connector/J
 *
 * TBD.
 */
public class Driver : PlanetscaleDriver() {
    public companion object {
        init {
            try {
                DriverManager.registerDriver(Driver())
            } catch (exc: SQLException) {
                throw RuntimeException("Can't register Planetscale driver", exc)
            }
        }
    }
}
