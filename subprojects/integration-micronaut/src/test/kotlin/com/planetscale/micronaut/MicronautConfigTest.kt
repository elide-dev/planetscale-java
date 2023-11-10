package com.planetscale.micronaut

import io.micronaut.context.ApplicationContext
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.yaml.snakeyaml.Yaml
import kotlin.test.*

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MicronautConfigTest {
    companion object {
        private const val TEST_BASIC = "test-basic.yml"
        private const val TEST_DISABLED = "test-disabled.yml"
        private const val TEST_MULTI_ENDPOINT = "test-multi-endpoint.yml"
    }

    private fun load(config: String): Map<String, Any> {
        return requireNotNull(this::class.java.getResourceAsStream("/test-configs/$config")).use {
            Yaml().load(it)
        }
    }

    private fun withConfig(cfg: String, op: (Map<String, Any>) -> Unit) {
        op(load(cfg))
    }

    private fun withContext(cfg: String, op: ApplicationContext.() -> Unit) {
        withConfig(cfg) {
            op(ApplicationContext.run(it))
        }
    }

    private fun withContext(cfg: Map<String, Any>, op: ApplicationContext.() -> Unit) {
        op(ApplicationContext.run(cfg))
    }

    private fun baseAssertions(it: PlanetscaleModuleConfiguration?) {
        assertNotNull(it, "should be able to resolve planetscale configuration via DI context")
        assertNotNull(it.isEnabled, "should not get `null` for `isEnabled`")
    }

    private fun combinedAssertions(it: PlanetscaleConfiguration?, enabled: Boolean = true) {
        assertNotNull(it, "should be able to resolve rendered/combined configuration from DI context")
        if (enabled) {
            assertTrue(it.isEnabled, "config should be enabled")
        } else {
            assertFalse(it.isEnabled, "config should be disabled")
        }
    }

    @Test fun testEmptyConfig() {
        val empty = mapOf<String, Any>()
        val cfg = mapOf("planetscale" to empty)
        val ctx = ApplicationContext.run(cfg)
        val ps = ctx.getBean(PlanetscaleModuleConfiguration::class.java)
        assertNotNull(ps, "should be able to resolve planetscale configuration via DI context")
    }

    @CsvSource(
        TEST_BASIC,
        TEST_DISABLED,
        TEST_MULTI_ENDPOINT,
    )
    @ParameterizedTest fun testParseConfigs(config: String) {
        assertDoesNotThrow {
            withConfig(config) {
                assertNotNull(it, "should be able to parse config")
                assertNotNull(
                    ApplicationContext.run(it).getBean(PlanetscaleModuleConfiguration::class.java),
                    "should be able to obtain configuration from DI context"
                )
                assertNotNull(
                    ApplicationContext.run(it).getBean(PlanetscaleConfiguration::class.java),
                    "should be able to obtain combined configuration from DI context"
                )
            }
        }
    }

    @Test fun testBasicConfig() = withContext(mapOf(
        "planetscale.enabled" to true,
        "planetscale.databases.default.db" to "dbname",
        "planetscale.databases.default.username" to "dbuser",
        "planetscale.databases.default.password" to "dbpass",
        "planetscale.databases.default.endpoints" to listOf("aws"),
        "planetscale.databases.default.features.boost" to true,
    )) {
        getBean(PlanetscaleModuleConfiguration::class.java).let {
            baseAssertions(it)
        }
        getBean(PlanetscaleConfiguration::class.java).let {
            combinedAssertions(it)
            assertNotNull(it.getProfile("default").orElse(null), "should find `default` profile")
            it.getProfile("default").orElseThrow().let { default ->
                assertNotNull(default, "should be able to resolve default db config")
                assertEquals("dbname", default.db, "db name should match configured value")
                assertEquals("dbuser", default.username, "db user should match configured value")
                assertEquals("dbpass", default.password, "db pass should match configured value")
                assertNotNull(default.endpoints, "should be able to resolve endpoints")
                assertTrue(default.endpoints.isNotEmpty(), "endpoints should not be empty")
                assertEquals(1, default.endpoints.size, "should have exactly-one configured endpoint")
                assertTrue(default.features.boost, "boost should be enabled")
            }
        }
    }

    @Test fun testDisabledConfig() = withContext(mapOf("planetscale.enabled" to false)) {
        getBean(PlanetscaleModuleConfiguration::class.java).let {
            baseAssertions(it)
            assertFalse(it.isEnabled, "config should be disabled (profile: $TEST_DISABLED)")
        }
        getBean(PlanetscaleConfiguration::class.java).let {
            combinedAssertions(it, enabled = false)
        }
    }

    @Test fun testBasicConfigJdbc() = withContext(mapOf(
        "planetscale.enabled" to true,
        "planetscale.databases.default.db" to "dbname",
        "planetscale.databases.default.username" to "dbuser",
        "planetscale.databases.default.password" to "dbpass",
        "planetscale.databases.default.endpoints" to listOf("aws"),
        "planetscale.databases.default.features.boost" to true,
    )) {
        getBean(PlanetscaleModuleConfiguration::class.java).let {
            baseAssertions(it)
        }
        getBean(PlanetscaleConfiguration::class.java).let {
            combinedAssertions(it)
            assertNotNull(it.getProfile("default").orElse(null), "should find `default` profile")
            it.getProfile("default").orElseThrow().let { default ->
                assertNotNull(default, "should be able to resolve default db config")
                assertEquals("dbname", default.db, "db name should match configured value")
                assertEquals("dbuser", default.username, "db user should match configured value")
                assertEquals("dbpass", default.password, "db pass should match configured value")
                assertNotNull(default.endpoints, "should be able to resolve endpoints")
                assertTrue(default.endpoints.isNotEmpty(), "endpoints should not be empty")
                assertEquals(1, default.endpoints.size, "should have exactly-one configured endpoint")
                assertTrue(default.features.boost, "boost should be enabled")

                // obtain a pre-built JDBC configuration
                val jdbc = assertNotNull(assertDoesNotThrow {
                    PlanetscaleConfigurationFactory.buildFrom(default)
                })
                assertEquals(jdbc.name, "default")
                assertEquals(jdbc.username, "dbuser")
                assertEquals(jdbc.password, "dbpass")
            }
        }
    }
}
