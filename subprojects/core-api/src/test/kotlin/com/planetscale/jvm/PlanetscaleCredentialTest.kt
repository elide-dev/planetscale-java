package com.planetscale.jvm

import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

/** Tests for [PlanetscaleCredential]. */
class PlanetscaleCredentialTest {
    @Test fun testExtractDatabaseName() {
        val uri = "planetscale://test/test?param=value"
        val db = PlanetscaleCredential.dbNameFromUri(URI.create(uri))
        assertEquals("test", db)
    }
}
