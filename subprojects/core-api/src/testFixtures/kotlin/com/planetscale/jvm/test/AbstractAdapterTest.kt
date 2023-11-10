package com.planetscale.jvm.test

import com.planetscale.jvm.PlanetscaleAdapter
import com.planetscale.jvm.PlanetscaleConfig
import com.planetscale.jvm.driver.AbstractPlanetscaleAdapter
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

abstract class AbstractAdapterTest<Adapter> where Adapter: AbstractPlanetscaleAdapter {
    /**
     * Create an adapter for testing.
     */
    abstract fun createAdapter(): Adapter

    @Test fun testObtain(): Unit = assertDoesNotThrow {
        assertNotNull(
            createAdapter(),
            "should be able to create testing adapter"
        )
    }

    @Test fun testDriverInterface(): Unit = createAdapter().let {
        assertNotNull(it.getMajorVersion(), "major version should not be null")
        assertNotNull(it.getMinorVersion(), "major version should not be null")
    }

    @Test fun testCreateUnderlyingDriver(): Unit = createAdapter().let {
        val config = PlanetscaleConfig.resolve(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@aws/hello"),
        )
        assertDoesNotThrow {
            it.configure(config)
        }
        assertDoesNotThrow {
            it.validate()
        }
        assertDoesNotThrow {
            it.renderedConnectionString(config)
        }
        assertDoesNotThrow {
            assertNotNull(it.createDriver())
        }
    }

    @Test fun testFailsWithNoConfig(): Unit = createAdapter().let {
        assertFailsWith<IllegalArgumentException> {
            it.validate()
        }
    }
}
