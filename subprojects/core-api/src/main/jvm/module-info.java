/**
 * # PlanetScale Connector/J
 *
 * <p> Wraps the MySQL Connector for Java with functionality specific to Planetscale, which leverages MySQL-compatible
 * cloud databasing via Vitess.</p>
 */
module planetscale.connector {
    requires java.base;
    requires java.sql;
    requires kotlin.stdlib;

    exports com.planetscale.jvm;
    exports com.planetscale.jvm.driver;

    provides java.sql.Driver with com.planetscale.Driver;
}
