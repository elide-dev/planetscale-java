package com.planetscale.jvm.driver

import com.planetscale.jvm.PlanetscaleAdapter
import com.planetscale.jvm.PlanetscaleCloudConnection
import com.planetscale.jvm.PlanetscaleConfig
import com.planetscale.jvm.PlanetscaleConnection
import java.io.Closeable
import java.net.URI
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Logger

/**
 * TBD.
 */
public abstract class AbstractPlanetscaleAdapter : PlanetscaleAdapter, Closeable, AutoCloseable {
    private val configuration: AtomicReference<PlanetscaleConfig> = AtomicReference(null)
    private val initialized: AtomicBoolean = AtomicBoolean(false)

    private val backing: Driver by lazy {
        createDriver()
    }

    override fun configure(config: PlanetscaleConfig) {
        if (!initialized.get()) {
            configuration.set(config)
            initialized.compareAndSet(false, true)
        }
    }

    override fun validate() {
        require(configuration.get() != null && initialized.get()) {
            "Cannot create Planetscale adapter without configuration"
        }
    }

    override fun connect(url: String, info: Properties?): Connection {
        return PlanetscaleConfig.resolve(PlanetscaleConfig.parseUri(url), info).let { config ->
            configure(config)
            config.connectBacking(config.toURI(), info)
        }
    }

    override fun close() {
        // nothing at this time
    }

    override fun getMajorVersion(): Int = withDriver { it.majorVersion }
    override fun getMinorVersion(): Int = withDriver { it.minorVersion }
    override fun getParentLogger(): Logger? = withDriver { it.parentLogger }
    override fun getPropertyInfo(url: String, info: Properties?): Array<DriverPropertyInfo> = withDriver {
        it.getPropertyInfo(url, info)
    }

    /**
     * TBD.
     */
    protected open fun <R> withDriver(op: (Driver) -> R): R {
        return op(backing)
    }

    /**
     * TBD.
     */
    public fun buildUri(config: PlanetscaleConfig): URI {
        return config.toURI()
    }

    /**
     * TBD.
     */
    protected open fun PlanetscaleConfig.connectBacking(uri: URI, info: Properties?): PlanetscaleConnection {
        return withDriver { driver ->
            driver.connect("${Constants.Prefix.JDBC}${toURI()}", info).let { connection ->
                PlanetscaleCloudConnection.create(this@AbstractPlanetscaleAdapter, connection)
            }
        }
    }

    /**
     * TBD.
     */
    override fun renderedConnectionString(config: PlanetscaleConfig): String {
        return StringBuilder().apply {
            append(Constants.Prefix.JDBC)
            append(buildUri(config).toString())
        }.toString()
    }

    /**
     * TBD.
     */
    public abstract fun createDriver(): Driver

    /**
     * TBD.
     */
    protected abstract fun PlanetscaleConfig.toURI(): URI
}
