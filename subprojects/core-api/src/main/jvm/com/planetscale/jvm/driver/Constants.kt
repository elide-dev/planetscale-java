package com.planetscale.jvm.driver

/**
 * TBD.
 */
public data object Constants {
    public const val MYSQL_PORT: Int = 3306
    public const val MYSQL_DRIVER: String = "com.mysql.cj.jdbc.Driver"
    public const val CONNECT_DOMAIN: String = "connect.psdb.cloud"
    public const val USER_VM_PROPERTY: String = "planetscale.username"
    public const val USER_ENV_VAR: String = "PLANETSCALE_USERNAME"
    public const val PASS_VM_PROPERTY: String = "planetscale.password"
    public const val PASS_ENV_VAR: String = "PLANETSCALE_PASSWORD"

    public data object DriverParams {
        public const val AUTO_RECONNECT: String = "autoReconnect"
        public const val CACHE_SERVER_CONFIG: String = "cacheServerConfiguration"
        public const val CACHE_RESULT_SET_METADATA: String = "cacheResultSetMetadata"
        public const val SSL_MODE: String = "sslMode"
        public const val SSL_MODE_VERIFY: String = "VERIFY_IDENTITY"
        public const val SESSION_VARIABLES: String = "sessionVariables"
        public const val ROUND_ROBIN_LOAD_BALANCE: String = "roundRobinLoadBalance"
    }

    public data object UrlTokens {
        public const val HOST_SEPARATOR: String = ".."
    }

    public data object StringValue {
        public const val TRUE: String = "true"
        public const val FALSE: String = "false"
    }

    public data object Prefix {
        public const val MYSQL: String = "jdbc:mysql:"
        public const val REPLICATION: String = "replication:"
        public const val H2: String = "jdbc:h2:mem"
        public const val PLANETSCALE: String = "jdbc:planetscale:"
    }

    public data object SymbolicHosts {
        public const val AWS: String = Provider.AWS
        public const val GCP: String = Provider.GCP
    }

    public data object Provider {
        public const val AWS: String = "aws"
        public const val GCP: String = "gcp"
        public const val GCP_CREDS_VAR: String = "GOOGLE_APPLICATION_CREDENTIALS"
        public const val AWS_CREDS_VAR: String = "AWS_ACCESS_KEY_ID"
    }
}
