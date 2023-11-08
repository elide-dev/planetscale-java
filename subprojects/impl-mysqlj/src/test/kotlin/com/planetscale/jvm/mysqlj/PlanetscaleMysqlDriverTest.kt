package com.planetscale.jvm.mysqlj

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/** Tests for the MySQL/J connector for the Planetscale driver. */
class PlanetscaleMysqlDriverTest {
    @Test fun testObtain() {
        assertDoesNotThrow { PlanetscaleMysqlDriver() }
    }

    @Test fun testAwsEndpoint() {
        assertEquals("aws.connect.psdb.cloud", PlanetscaleMysqlDriver.awsEndpoint())
        assertEquals("example.aws.connect.psdb.cloud", PlanetscaleMysqlDriver.awsEndpoint("example"))
    }

    @Test fun testGcpEndpoint() {
        assertEquals("gcp.connect.psdb.cloud", PlanetscaleMysqlDriver.gcpEndpoint())
        assertEquals("example.gcp.connect.psdb.cloud", PlanetscaleMysqlDriver.gcpEndpoint("example"))
    }

    @Test fun testDetectProvider(): Unit = assertDoesNotThrow {
        PlanetscaleMysqlDriver.detectProvider()
    }

    @Test fun testResolveHostSymbolAws() {
        assertNotNull(PlanetscaleMysqlDriver.resolveHostSymbols("aws"))
        assertEquals("aws.connect.psdb.cloud", PlanetscaleMysqlDriver.resolveHostSymbols("aws"))
        assertNotNull(PlanetscaleMysqlDriver.resolveHostSymbols("us-west-1.aws"))
        assertEquals(
            "example.aws.connect.psdb.cloud",
            PlanetscaleMysqlDriver.resolveHostSymbols("example.aws"),
        )
    }

    @Test fun testResolveHostSymbolsGcp() {
        assertNotNull(PlanetscaleMysqlDriver.resolveHostSymbols("gcp"))
        assertEquals("gcp.connect.psdb.cloud", PlanetscaleMysqlDriver.resolveHostSymbols("gcp"))
        assertNotNull(PlanetscaleMysqlDriver.resolveHostSymbols("us-west-1.gcp"))
        assertEquals(
            "example.gcp.connect.psdb.cloud",
            PlanetscaleMysqlDriver.resolveHostSymbols("example.gcp"),
        )
    }

    @Test fun testResolveHostSymbolsCustom() {
        assertNotNull(
            PlanetscaleMysqlDriver.resolveHostSymbols("something-custom.com")
        )
        assertEquals(
            "something-custom.com",
            PlanetscaleMysqlDriver.resolveHostSymbols("something-custom.com"),
        )
    }
}
