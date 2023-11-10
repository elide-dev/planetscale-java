package com.planetscale.jvm.h2

import com.planetscale.jvm.PlanetscaleConfig
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class PlanetscaleH2DriverTest {
    @Test fun testObtain() {
        assertDoesNotThrow { PlanetscaleH2Driver() }
    }

    @Test fun testCreateDriver() {
        assertDoesNotThrow {
            assertNotNull(PlanetscaleH2Driver().createDriver())
        }
    }

    @Test fun testCalculateH2URI(): Unit = PlanetscaleH2Driver().let {
        assertNotNull(it, "should be able to construct h2 driver impl from scratch")
        val config = PlanetscaleConfig.of(
            PlanetscaleConfig.parseUri("jdbc:planetscale://aws/hello"),
            com.planetscale.jvm.PlanetscaleCredential.user("hello", "user", "pass"),
        )
        assertDoesNotThrow {
            it.configure(config)
        }
        assertDoesNotThrow {
            it.validate()
        }
        val uri = assertDoesNotThrow {
            it.buildUri(config)
        }
        assertNotNull(uri, "should not get `null` DB URI from database driver")
    }

    @Test fun testFailsWithNoConfig(): Unit = PlanetscaleH2Driver().let {
        assertFailsWith<IllegalArgumentException> {
            it.validate()
        }
    }
}
