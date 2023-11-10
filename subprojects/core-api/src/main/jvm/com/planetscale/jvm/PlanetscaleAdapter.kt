package com.planetscale.jvm

import com.planetscale.jvm.err.PlanetscaleError
import java.io.Closeable
import java.sql.Connection
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.logging.Logger

/**
 * # Planetscale Adapter
 *
 * Defines the API surface of internal logic for the Planetscale/J adapter; implementations of this interface must
 * support querying on top of a backing database driver, like MySQL/J or H2 (for internal testing). The Planetscale
 * adapter then manages the lifecycle of the underlying driver, connections, and so on.
 *
 * &nbsp;
 *
 * ## Usage
 *
 * An implementation, such as `impl-mysqlj`, or `impl-h2`, must be included on the classpath and module path. The first
 * implementation that is found is used through the regular adapter factory; it is an error to have multiple adapters on
 * the classpath without declaring the adapter to use.
 *
 * &nbsp;
 *
 * ### Declaring an Adapter
 *
 * The following methods are supported for declaring a forced implementation:
 *
 * - `PLANETSCALE_J_ADAPTER` environment variable, which must be set to one of `mysql` or `h2`.
 * - `planetscale.adapter` VM property, which must be set to one of `mysql` or `h2`.
 *
 * &nbsp;
 *
 * ### Using from JDBC
 *
 * For regular adapter users, the adapter is used transparently from JDBC. JDBC URLs with the prefix `planetscale` are
 * registered for support through the backing driver implementation. JDBC URLs for this driver are expected to be
 * expressed in the following format:
 *
 * #### JDBC URL Format
 *
 * - All driver URLs start with `jdbc:planetscale:`.
 * - Next, following standard JDBC URL structure, the username and password are provided, as `username:password@`.
 * - Next, the URL declares a connection symbol; valid connection symbols are defined in the next section.
 * - The database name is provided, after a separating `/`
 * - Finally, additional driver parameters are provided, as applicable, in URL param format.
 *
 * &nbsp;
 *
 * #### Connection Symbols
 *
 * The driver is able to determine the optimal connection string to use based on the current cloud provider and database
 * location. Thus, either the location must be provided, or the driver uses a generic connection endpoint.
 *
 * In addition to fully qualified domains provided within the Planetscale UI, the following special values are supported
 * for smart connection management:
 *
 * - `cloud`: Connects to a generic, globally accelerated endpoint for Planetscale. If a provider can be detected in use
 *   (such as AWS), the connection string may be customized further by the driver.
 *
 * - `<provider>.<region>`: Provider string (available options are `aws` or `gcp`), and the region of the database; when
 *   a connection symbol is provided in this format, an endpoint is used which is specific to the provider and region.
 *
 * &nbsp;
 *
 * ### Driver Parameters
 *
 * This driver exposes several parameters, and allows control of the underlying driver through parameters documented by
 * the MySQL Connector/J. The following parameters are supported:
 *
 * - (TBD).
 */
public interface PlanetscaleAdapter : Closeable, AutoCloseable {
    /**
     * TBD.
     */
    public fun configure(config: PlanetscaleConfig)

    /**
     * TBD.
     */
    @Throws(PlanetscaleError::class)
    public fun validate()

    /**
     * TBD.
     */
    public fun getMajorVersion(): Int

    /**
     * TBD.
     */
    public fun getMinorVersion(): Int

    /**
     * TBD.
     */
    public fun renderedConnectionString(config: PlanetscaleConfig): String

    /**
     * TBD.
     */
    public fun getPropertyInfo(url: String, info: Properties?): Array<DriverPropertyInfo>

    /**
     * TBD.
     */
    public fun getParentLogger(): Logger

    /**
     * TBD.
     */
    public fun connect(url: String, info: Properties? = null): Connection
}
