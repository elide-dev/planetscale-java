package com.planetscale.jvm.test

import com.planetscale.jvm.PlanetscaleConfig
import com.planetscale.jvm.driver.AbstractPlanetscaleAdapter
import org.junit.jupiter.api.assertDoesNotThrow
import java.sql.SQLFeatureNotSupportedException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

abstract class AbstractAdapterTest<Adapter> where Adapter: AbstractPlanetscaleAdapter {
    /**
     * Create an adapter for testing.
     */
    abstract fun createAdapter(config: PlanetscaleConfig): Adapter

    /**
     * TBD.
     */
    protected open fun obtain(
        connectionString: String? = null,
        configure: Boolean = true,
    ): Adapter {
        PlanetscaleConfig.resolve(
            PlanetscaleConfig.parseUri(
                connectionString ?: "jdbc:planetscale://user:pass@aws/hello"
            ),
        ).let { config ->
            createAdapter(config).let {
                if (configure) {
                    assertDoesNotThrow {
                        it.configure(config)
                    }
                    assertDoesNotThrow {
                        it.validate()
                    }
                }
                return it
            }
        }
    }

    /**
     * Whether to connect and actually run queries.
     */
    open fun connectSupported(): Boolean = false

    @Test fun testObtain(): Unit = assertDoesNotThrow {
        assertNotNull(
            obtain(),
            "should be able to create testing adapter"
        )
    }

    @Test fun testDriverInterface(): Unit = obtain().let {
        assertNotNull(it.getMajorVersion(), "major version should not be null")
        assertNotNull(it.getMinorVersion(), "major version should not be null")
    }

    @Test fun testCreateUnderlyingDriver(): Unit = obtain().let {
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

    @Test fun testFailsWithNoConfig(): Unit = obtain(configure = false).let {
        assertFailsWith<IllegalArgumentException> {
            it.validate()
        }
    }

    @Test fun testCreateUse() {
        assertDoesNotThrow {
            obtain().use { adapter ->
                assertNotNull(adapter)
            }
        }
    }

    @Test fun testParentLogger() {
        try {
            obtain().getParentLogger()
        } catch (e: SQLFeatureNotSupportedException) {
            // expected, it's okay if it isn't supported
        }
    }
}
