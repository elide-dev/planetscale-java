package com.planetscale

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.ServiceLoader
import kotlin.test.*

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

    @Test fun testDriverCompliance() {
        assertTrue(testDriver.jdbcCompliant(), "driver should indicate jdbc compliance")
    }

    @Test fun testDriverUse(): Unit = Driver().use {
        assertNotNull(it, "should be able to obtain test driver and close it")
    }

    @CsvSource(
        "jdbc:planetscale://aws/hello,true",
        "jdbc:planetscale://user:pass@aws/hello,true",
        "jdbc:planetscale://user:pass@aws/hello?something=hi,true",
        "jdbc:mysql://testing123/hello,false",
        "jdbc:mysql://user:pass@aws/hello,false",
        "jdbc:mysql://user:pass@aws/hello?something=hi,false",
    )
    @ParameterizedTest
    fun testAcceptsUrl(url: String, accepts: Boolean) {
        assertEquals(
            accepts,
            testDriver.acceptsURL(url),
            "`acceptsURL` should behave as expected"
        )
    }
}
