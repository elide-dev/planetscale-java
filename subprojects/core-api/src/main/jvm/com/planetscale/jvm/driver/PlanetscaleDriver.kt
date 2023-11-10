package com.planetscale.jvm.driver

import com.planetscale.jvm.PlanetscaleAdapter
import java.io.Closeable
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Logger
import java.util.stream.Collectors

/**
 * TBD.
 */
public abstract class PlanetscaleDriver : Driver, Closeable, AutoCloseable {
    private companion object {
        @JvmStatic fun resolveImpl(explicit: String? = null): PlanetscaleAdapter {
            val serviceImpls = ServiceLoader.load(PlanetscaleAdapter::class.java)
                .stream()
                .collect(Collectors.toUnmodifiableList())

            require(serviceImpls.isNotEmpty()) { "Failed to find any implementation for the Planetscale adapter" }

            return serviceImpls.firstOrNull {
                explicit == null || it.get().javaClass.canonicalName == explicit
            }?.get() ?: error("No suitable Planetscale driver (implementation: ${explicit ?: "auto"})")
        }
    }

    // Active adapter and state.
    private val activeAdapter: AtomicReference<PlanetscaleAdapter> = AtomicReference(null)
    private val initialized: AtomicBoolean = AtomicBoolean(false)

    private fun obtainOrInitializeDriver() {
        if (!initialized.get()) {
            synchronized(this) {
                activeAdapter.set(resolveImpl(System.getProperty("planetscale.driver")))
                initialized.compareAndSet(false, true)
            }
        }
    }

    private fun <R> withAdapter(op: PlanetscaleAdapter.() -> R): R {
        obtainOrInitializeDriver()
        return activeAdapter.get().op()
    }

    private fun obtainConnection(url: String, info: Properties?): Connection {
        obtainOrInitializeDriver()
        return withAdapter {
            connect(url, info)
        }
    }

    // -- API: JDBC Driver -- //

    override fun connect(url: String, info: Properties?): Connection = obtainConnection(url, info)
    override fun getMajorVersion(): Int = withAdapter { getMajorVersion() }
    override fun getMinorVersion(): Int = withAdapter { getMinorVersion() }
    override fun jdbcCompliant(): Boolean = true // both H2 and mysql are JDBC compliant
    override fun acceptsURL(url: String?): Boolean = url?.startsWith(Constants.Prefix.PLANETSCALE) ?: false
    override fun getParentLogger(): Logger? = withAdapter { getParentLogger() }
    override fun close(): Unit = withAdapter { close() }
    override fun getPropertyInfo(url: String, info: Properties?): Array<DriverPropertyInfo> = withAdapter {
        getPropertyInfo(url, info)
    }
}
