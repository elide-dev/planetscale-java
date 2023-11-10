/**
 * # PlanetScale Connector/J
 *
 * <p> Wraps the MySQL Connector for Java with functionality specific to Planetscale, which leverages MySQL-compatible
 * cloud databasing via Vitess.</p>
 */
module planetscale.driver {
    requires java.base;
    requires java.sql;
    requires kotlin.stdlib;
    requires planetscale.connector;
    requires planetscale.connector.mysqlj;

    exports com.planetscale;
    provides java.sql.Driver with com.planetscale.Driver;
}
