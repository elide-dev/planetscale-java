package com.planetscale

import org.junit.jupiter.api.assertDoesNotThrow
import java.util.ServiceLoader
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Tests for the main driver facade, at [com.planetscale.Driver]. */
class DriverTest {
    private val testDriver: Driver by lazy {
        Driver()
    }

    private fun withDriver(op: Driver.() -> Unit) {
        assertNotNull(testDriver, "should be able to obtain test driver")
        op.invoke(testDriver)
    }

    @Test fun testDriverConstruct() {
        assertDoesNotThrow {
            Driver()
        }
    }

    @Test fun testDriverAPI() = withDriver {
        assertNotNull(majorVersion, "majorVersion should not return `null`")
        assertNotNull(minorVersion, "majorVersion should not return `null`")
    }

    @Test fun testDriverMajorVersion() = withDriver {
        assertNotNull(majorVersion, "majorVersion should not return `null`")
        assertNotEquals(0, majorVersion, "majorVersion should not be `0`")
    }

    @Test fun testDriverMinorVersion() = withDriver {
        assertNotNull(minorVersion, "minorVersion should not return `null`")
        assertNotEquals(0, minorVersion, "minorVersion should not be `0`")
    }

    @Test fun testDriverServiceLoader() {
        val all = ServiceLoader.load(java.sql.Driver::class.java)
            .toList()
            .map { it::class }

        assertNotEquals(0, all.size, "should have at least one driver")
        assertTrue(all.contains(Driver::class), "should contain our driver")
    }
}
