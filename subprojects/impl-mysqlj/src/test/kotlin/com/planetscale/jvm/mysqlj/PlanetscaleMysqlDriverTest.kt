package com.planetscale.jvm.mysqlj

import com.planetscale.jvm.PlanetscaleConfig
import com.planetscale.jvm.test.AbstractAdapterTest
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/** Tests for the MySQL/J connector for the Planetscale driver. */
class PlanetscaleMysqlDriverTest : AbstractAdapterTest<PlanetscaleMysqlDriver>() {
    override fun createAdapter(): PlanetscaleMysqlDriver {
        return PlanetscaleMysqlDriver()
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

    @Test fun testCalculateMySQLURI() = PlanetscaleMysqlDriver().let {
        assertNotNull(it, "should be able to construct mysql driver impl from scratch")

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
        val str = uri.toString()
        assertFalse(str.startsWith("jdbc:"), "calculated DB URL should NOT have `jdbc` prefix")
        assertTrue(str.startsWith("mysql:"), "calculated DB URL should have `mysql:` prefix")
        val hosts = str
            .substringAfter("://")
            .substringAfter("@")
            .substringBefore("/")
            .split(",")
        assertEquals(1, hosts.size, "should find one host in single-host URL")
        assertEquals("aws.connect.psdb.cloud:3306", hosts.first(), "host should be expected value")
        assertEquals("/hello", uri.path, "DB name should be included properly")
    }

    @Test fun testCalculateMySQLURIWithCredential() = PlanetscaleMysqlDriver().let {
        assertNotNull(it, "should be able to construct mysql driver impl from scratch")

        val config = PlanetscaleConfig.resolve(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@aws/hello"),
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
        val str = uri.toString()
        assertFalse(str.startsWith("jdbc:"), "calculated DB URL should NOT have `jdbc` prefix")
        assertTrue(str.startsWith("mysql:"), "calculated DB URL should have `mysql:` prefix")
        val hosts = str
            .substringAfter("://")
            .substringAfter("@")
            .substringBefore("/")
            .split(",")

        assertNotNull(uri.userInfo, "should be able to find user info in credential-based URI")
        assertEquals(
            "user",
            uri.userInfo.split(":").first(),
            "should find expected user in credential-based URI"
        )
        assertEquals(
            "pass",
            uri.userInfo.split(":").last(),
            "should find expected password in credential-based URI"
        )
        assertEquals(1, hosts.size, "should find one host in single-host URL")
        assertEquals("aws.connect.psdb.cloud:3306", hosts.first(), "host should be expected value")
        assertEquals("/hello", uri.path, "DB name should be included properly")
    }

    @Test fun testRenderConnectionStringWithCredentials() = PlanetscaleMysqlDriver().let {
        assertNotNull(it, "should be able to construct mysql driver impl from scratch")

        val config = PlanetscaleConfig.resolve(
            PlanetscaleConfig.parseUri("jdbc:planetscale://user:pass@aws/hello"),
        )
        assertDoesNotThrow {
            it.configure(config)
        }
        assertDoesNotThrow {
            it.validate()
        }
        val uri = assertDoesNotThrow {
            it.renderedConnectionString(config)
        }
        assertNotNull(uri, "should not get `null` DB URI from database driver")
        assertTrue(
            uri.startsWith("jdbc:"),
            "rendered DB connection string should have `jdbc` prefix, but got: $uri",
        )
        assertTrue(
            uri.startsWith("jdbc:mysql:"),
            "rendered DB connection string should have `jdbc:mysql:` prefix, but got: $uri",
        )
        val userInfo = uri
            .substringAfter("://")
            .substringBefore("@")
        val user = userInfo.split(":").first()
        val pass = userInfo.split(":").last()
        val dbname = uri
            .substringAfter("://")
            .substringAfter("@")
            .substringAfter("/")
            .substringBefore("?")
        val hosts = uri
            .substringAfter("://")
            .substringAfter("@")
            .substringBefore("/")
            .split(",")

        assertNotNull(userInfo, "should be able to find user info in credential-based connection string")
        assertEquals(
            "user",
            user,
            "should find expected user in credential-based connection string"
        )
        assertEquals(
            "pass",
            pass,
            "should find expected password in credential-based connection string"
        )
        assertEquals(1, hosts.size, "should find one host in single-host URL")
        assertEquals("aws.connect.psdb.cloud:3306", hosts.first(), "host should be expected value")
        assertEquals("hello", dbname, "DB name should be included in connection string")
    }
}
