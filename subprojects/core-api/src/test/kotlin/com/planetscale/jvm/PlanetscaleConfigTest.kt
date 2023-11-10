package com.planetscale.jvm

import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/** Tests for Planetscale configuration classes, like [PlanetscaleConfig]. */
class PlanetscaleConfigTest {
    @Test fun testConfigDefaults() {
        val cfg = PlanetscaleConfig.defaults(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@us-west-1.aws/dbname"),
            PlanetscaleCredential.user("dbname", "user", "pass"),
        )
        assertNotNull(cfg, "config should not be null")
        assertNotNull(cfg.uri, "URI in config should not be null")
        assertNotNull(cfg.credential, "credential in config should not be null")
        assertNotNull(cfg.enableBoost(), "`enableBoost` should never return null")
        assertNotNull(cfg.multiHost(), "`multiHost` should never return null")
        assertNotNull(cfg.targetHosts(), "`targetHosts` should never return null")
        assertTrue(cfg.targetHosts().isNotEmpty(), "`targetHosts` should not be empty")
        assertNotNull(cfg.credential, "should have credential record")
        assertEquals(cfg.credential!!.database, "dbname", "database name should be extracted correctly")
        assertEquals(cfg.credential!!.username, "user", "username should be extracted correctly")
        assertEquals(cfg.credential!!.password, "pass", "password should be extracted correctly")
        assertEquals(cfg.targetHosts().size, 1, "should have 1 target host")
        assertEquals(cfg.targetHosts().first(), "us-west-1.aws")
        assertFalse(cfg.enableBoost(), "boost should default to false")
    }

    @Test fun testConfigManual() {
        val cfg = PlanetscaleConfig.of(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@us-west-1.aws/dbname"),
            PlanetscaleCredential.user("dbname", "user", "pass"),
        )
        assertNotNull(cfg, "config should not be null")
        assertNotNull(cfg.uri, "URI in config should not be null")
        assertNotNull(cfg.credential, "credential in config should not be null")
        assertNotNull(cfg.enableBoost(), "`enableBoost` should never return null")
        assertNotNull(cfg.multiHost(), "`multiHost` should never return null")
        assertNotNull(cfg.targetHosts(), "`targetHosts` should never return null")
        assertTrue(cfg.targetHosts().isNotEmpty(), "`targetHosts` should not be empty")
        assertNotNull(cfg.credential, "should have credential record")
        assertEquals(cfg.credential!!.database, "dbname", "database name should be extracted correctly")
        assertEquals(cfg.credential!!.username, "user", "username should be extracted correctly")
        assertEquals(cfg.credential!!.password, "pass", "password should be extracted correctly")
        assertEquals(cfg.targetHosts().size, 1, "should have 1 target host")
        assertEquals(cfg.targetHosts().first(), "us-west-1.aws")
        assertFalse(cfg.enableBoost(), "boost should default to false")
    }

    @Test fun testConfigResolveFromUri() {
        val cfg = PlanetscaleConfig.resolve(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@us-west-1.aws/dbname"),
        )
        assertNotNull(cfg, "config should not be null")
        assertNotNull(cfg.uri, "URI in config should not be null")
        assertNotNull(cfg.credential, "credential in config should not be null")
        assertNotNull(cfg.enableBoost(), "`enableBoost` should never return null")
        assertNotNull(cfg.multiHost(), "`multiHost` should never return null")
        assertNotNull(cfg.targetHosts(), "`targetHosts` should never return null")
        assertTrue(cfg.targetHosts().isNotEmpty(), "`targetHosts` should not be empty")
        assertNotNull(cfg.credential, "should have credential record")
        assertEquals(cfg.credential!!.database, "dbname", "database name should be extracted correctly")
        assertEquals(cfg.credential!!.username, "user", "username should be extracted correctly")
        assertEquals(cfg.credential!!.password, "pass", "password should be extracted correctly")
        assertEquals(cfg.targetHosts().size, 1, "should have 1 target host")
        assertEquals(cfg.targetHosts().first(), "us-west-1.aws")
        assertFalse(cfg.enableBoost(), "boost should default to false")
    }

    @Test fun testConfigResolveFromUriMultiHost() {
        val cfg = PlanetscaleConfig.resolve(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@us-west-1.aws..aws/dbname"),
        )
        assertNotNull(cfg, "config should not be null")
        assertNotNull(cfg.uri, "URI in config should not be null")
        assertNotNull(cfg.credential, "credential in config should not be null")
        assertNotNull(cfg.enableBoost(), "`enableBoost` should never return null")
        assertNotNull(cfg.multiHost(), "`multiHost` should never return null")
        assertNotNull(cfg.targetHosts(), "`targetHosts` should never return null")
        assertTrue(cfg.targetHosts().isNotEmpty(), "`targetHosts` should not be empty")
        assertNotNull(cfg.credential, "should have credential record")
        assertEquals(cfg.credential!!.database, "dbname", "database name should be extracted correctly")
        assertEquals(cfg.credential!!.username, "user", "username should be extracted correctly")
        assertEquals(cfg.credential!!.password, "pass", "password should be extracted correctly")
        assertEquals(cfg.targetHosts().size, 2, "should have 2 target hosts")
        assertEquals(cfg.targetHosts().first(), "us-west-1.aws")
        assertEquals(cfg.targetHosts()[1], "aws")
        assertFalse(cfg.enableBoost(), "boost should default to false")
    }

    @Test fun testConfigKnownOptions() {
        val cfg = PlanetscaleConfig.resolve(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@us-west-1.aws..aws/dbname?enableBoost=true"),
        )
        assertNotNull(cfg, "config should not be null")
        assertNotNull(cfg.uri, "URI in config should not be null")
        assertNotNull(cfg.credential, "credential in config should not be null")
        assertNotNull(cfg.enableBoost(), "`enableBoost` should never return null")
        assertNotNull(cfg.multiHost(), "`multiHost` should never return null")
        assertNotNull(cfg.targetHosts(), "`targetHosts` should never return null")
        assertTrue(cfg.targetHosts().isNotEmpty(), "`targetHosts` should not be empty")
        assertNotNull(cfg.credential, "should have credential record")
        assertEquals(cfg.credential!!.database, "dbname", "database name should be extracted correctly")
        assertEquals(cfg.credential!!.username, "user", "username should be extracted correctly")
        assertEquals(cfg.credential!!.password, "pass", "password should be extracted correctly")
        assertEquals(cfg.targetHosts().size, 2, "should have 2 target hosts")
        assertEquals(cfg.targetHosts().first(), "us-west-1.aws")
        assertEquals(cfg.targetHosts()[1], "aws")
        assertTrue(cfg.enableBoost(), "boost should be active")
    }

    @Test fun testConfigPassthrough() {
        val cfg = PlanetscaleConfig.resolve(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@us-west-1.aws..aws/dbname?someParam=hi"),
        )
        assertNotNull(cfg, "config should not be null")
        assertNotNull(cfg.uri, "URI in config should not be null")
        assertNotNull(cfg.credential, "credential in config should not be null")
        assertNotNull(cfg.enableBoost(), "`enableBoost` should never return null")
        assertNotNull(cfg.multiHost(), "`multiHost` should never return null")
        assertNotNull(cfg.targetHosts(), "`targetHosts` should never return null")
        assertTrue(cfg.targetHosts().isNotEmpty(), "`targetHosts` should not be empty")
        assertNotNull(cfg.credential, "should have credential record")
        assertEquals(cfg.credential!!.database, "dbname", "database name should be extracted correctly")
        assertEquals(cfg.credential!!.username, "user", "username should be extracted correctly")
        assertEquals(cfg.credential!!.password, "pass", "password should be extracted correctly")
        assertEquals(cfg.targetHosts().size, 2, "should have 2 target hosts")
        assertEquals(cfg.targetHosts().first(), "us-west-1.aws")
        assertEquals(cfg.targetHosts()[1], "aws")
        assertFalse(cfg.enableBoost(), "boost should default to false")
        assertTrue(cfg.extraParams().isNotEmpty(), "should have extra params")
        assertTrue(cfg.extraParams().contains("someParam"), "unrecognized param should be in `someParams`")
        assertEquals(cfg.extraParams()["someParam"], "hi", "`someParam` should be set to `hi`")
    }

    @Test fun testFailParseNonJDBCUrl() {
        assertThrows<IllegalArgumentException> {
            // cannot parse non-jdbc urls
            PlanetscaleConfig.parseUri("https://example.com")
        }
        assertThrows<IllegalArgumentException> {
            // missing jdbc prefix
            PlanetscaleConfig.parseUri("mysql://hello/hi")
        }
    }
}
