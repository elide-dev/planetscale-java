package com.planetscale.jvm.driver

import kotlin.test.Test
import kotlin.test.assertNotNull

/** Tests for build-time constants. */
class ConstantsTest {
    @Test fun testConstantObjects() {
        listOf(
            Constants,
            Constants.DriverParams,
            Constants.UrlTokens,
            Constants.StringValue,
            Constants.Prefix,
            Constants.SymbolicHosts,
            Constants.Provider,
        ).forEach {
            assertNotNull(it)
            assertNotNull(it.toString())
        }
    }
}
