package com.planetscale.jvm.h2

import com.planetscale.jvm.PlanetscaleConfig
import com.planetscale.jvm.test.AbstractAdapterTest
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class PlanetscaleH2DriverTest : AbstractAdapterTest<PlanetscaleH2Driver>() {
    override fun createAdapter(config: PlanetscaleConfig): PlanetscaleH2Driver {
        return PlanetscaleH2Driver()
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
}
