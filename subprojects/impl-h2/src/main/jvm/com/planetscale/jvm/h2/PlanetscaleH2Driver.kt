package com.planetscale.jvm.h2

import com.planetscale.jvm.PlanetscaleConfig
import com.planetscale.jvm.driver.AbstractPlanetscaleAdapter
import com.planetscale.jvm.driver.Constants
import java.net.URI
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.logging.Logger

/**
 * TBD.
 */
public class PlanetscaleH2Driver : AbstractPlanetscaleAdapter() {
    private companion object {
        private const val H2_DRIVER = "org.h2.Driver"
    }

    override fun PlanetscaleConfig.toURI(): URI {
        return URI.create(
            "jdbc:h2:mem:${credential.database};MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;"
        )
    }

    override fun createDriver(): Driver {
        return DriverManager.drivers().filter {
            it.javaClass.canonicalName == H2_DRIVER
        }.findFirst().orElse(null) ?: error(
            "Failed to resolve H2 driver: check your classpath?"
        )
    }
}
