package com.planetscale.jvm

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PlanetscaleParameterTest {
    private fun baseAssertions(param: PlanetscaleParameter<*>) {
        assertNotNull(param)
        assertNotNull(param.key)
        assertNotNull(param.param)
        assertNotNull(param.source)
        assertDoesNotThrow { param.value }
    }

    @Test fun testEnableBoost() {
        val param = PlanetscaleParameter.EnableBoost.of(true)
        baseAssertions(param)
        assertTrue(param.value)
        val param2 = PlanetscaleParameter.EnableBoost.enabled()
        baseAssertions(param2)
        assertTrue(param2.value)
    }
}
