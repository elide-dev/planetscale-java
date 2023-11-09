
# Planetscale Connector/J

> **Note**
> Under construction; use at your own risk

This library implements a [JDBC adapter](https://www.oracle.com/database/technologies/appdev/jdbc.html) for
[Planetscale][1].

Planetscale uses [Vitess][2] as a backend, which is wire-compatible with MySQL; thus, this library is a wrapper around
the [MySQL Connector/J][3] library, which is used for the actual work of connecting and running queries.

This library adds several features, using the MySQL/J connector, to make it easier to work with Planetscale.

- [Installation](#installation)
- [Features](#features)
- [Integration Libraries](#integration-libraries)
  - [Micronaut Integration](#micronaut-integration)
  - [Kotlin Integration](#kotlin-integration)
  - [GraalVM Integration](#graalvm-integration)

## Installation

> **Warning**
> This is *not* endorsed by Planetscale, and is not yet published to Maven Central.

| Coordinates                           | Driver                       |
|---------------------------------------|------------------------------|
| ```com.planetscale:planetscale-jvm``` | ```com.planetscale.Driver``` |

**Gradle (Kotlin DSL, via Version Catalogs):**
```kotlin
// in settings.gradle.kts

dependencyResolutionManagement {
    versionCatalogs {
        create("planetscale") {
            from("com.planetscale:planetscale-catalog:(latest version)")
        }
    }
}
```
```kotlin
// in build.gradle.kts

dependencies {
    implementation(planetscale.driver)
}
```

**Gradle (Kotlin DSL, one library at a time):**
```kotlin
dependencies {
    implementation("com.planetscale:planetscale-jvm:<version>")
}
```

**Maven:**
```xml
<dependency>
    <groupId>com.planetscale</groupId>
    <artifactId>planetscale-jvm</artifactId>
    <version><!-- use the latest version --></version>
</dependency>
```

# Features

- **Easy Planetscale connection strings.**

  Use `jdbc:planetscale://aws` or `jdbc:planetscale://gcp` or connect to a specific region with something like
  `jdbc:planetscale://us-east-1.aws`. The adapter builds the right URL for you. These examples become:

```
jdbc:mysql://aws.connect.psdb.cloud:3306/dbname?...
jdbc:mysql://gcp.connect.psdb.cloud:3306/dbname?...
jdbc:mysql://us-east-1.aws.connect.psdb.cloud:3306/dbname?...
```


- **Load balanced reads.**

  Use more than one endpoint with URLs like: `jdbc:planetscale://us-west-1.aws..aws`. In this case, the MySQL/J driver
  is configured with:

```
jdbc:mysql:replication://us-west-1.aws.connect.psdb.cloud:3306,aws.connect.psdb.cloud:3306/dbname?roundRobinLoadBalance=true&...
```


- **Eager driver cache and solid defaults.**

  The Planetscale driver configures the MySQL/J driver for client-side caching of server configuration, result set
  metadata, and so on. SSL verification is turned on, as is auto-reconnection. Equivalent driver options:

```
autoReconnect=true
cacheServerConfiguration=true
cacheResultSetMetadata=true
sslMode=VERIFY_IDENTITY
roundRobinLoadBalance=true
```


- **Support for Planetscale's options.**

  Planetscale options like `enableBoost` can be applied to configure the underlying URL properly. For example, adding
  `enableBoost=true` to the AWS sample URL above, like so: `jdbc:planetscale://aws?enableBoost=true`, becomes:

```
jdbc:mysql://aws.connect.psdb.cloud:3306/dbname?sessionVariables=boost_cached_queries=true&...
```


- **Configure credentials via env vars, properties, or connection URL.**

  `PLANETSCALE_USERNAME`, `-Dplanetscale.username`, and `jdbc:planetscale://user:pass@...` are all supported, as are the 
  same variables for the connection password. These are consulted if no user/pass are found in the connection string.

# Integration Libraries

There are a few libraries shipped with this repo which make integration easier with various frameworks or systems:

## Micronaut Integration

Adds [Micronaut][5] property source support for Planetscale configuration. Effectively, the module configures itself as
a driver (when enabled), and provides connection parameters as configuration. Otherwise the driver is identical to using
the MySQL/J driver library directly.


### Usage

Configuration in Micronaut is pretty easy:

```yaml
planetscale:
  enabled: true
  databases:
    default:
        db: dbname
        username: username-here
        password: password-here
        endpoints:
          - us-west-1.aws
          - aws
        features:
          boost: true
```

`PLANETSCALE_DB`, `-Dplanetscale.db`, and so on, also work. This is roughly equivalent to:

```yaml
datasources:
  default:
    url: jdbc:mysql://username-here:password-here@us-west-1.aws.connect.psdb.cloud:3306,aws.connect.psdb.cloud:3306/dbname?cacheServerConfiguration=true&cacheResultSetMetadata=true&sslMode=VERIFY_IDENTITY&roundRobinLoadBalance=true&sessionVariables=boost_cached_queries=true
    driverClassName: com.planetscale.Driver
    db-type: mysql
    name: dbname
    username: username-here
    password: password-here
    maximum-pool-size: 32
```

### Installation

**Coordinates:**
```
com.planetscale:planetscale-micronaut
```

**Maven:**
```xml
<dependency>
    <groupId>com.planetscale</groupId>
    <artifactId>planetscale-micronaut</artifactId>
    <version><!-- use the latest version --></version>
</dependency>
```

**Gradle (Version Catalog):**
```kotlin
dependencies {
    implementation(planetscale.integration.micronaut)
}
```

## Kotlin Integration

Adds [Kotlin][6]-focused API support for Planetscale. Easily load credentials and configuration, and then obtain a driver
instance or connection.

### Installation

**Coordinates:**
```
com.planetscale:planetscale-kotlin
```

**Maven:**
```xml
<dependency>
    <groupId>com.planetscale</groupId>
    <artifactId>planetscale-kotlin</artifactId>
    <version><!-- use the latest version --></version>
</dependency>
```

**Gradle (Version Catalog):**
```kotlin
dependencies {
    implementation(planetscale.integration.kotlin)
}
```


## GraalVM Integration

Adds metadata and a supporting feature configuration for [GraalVM][4] native image compilation. Reflection configuration
is embedded which supports both this adapter and MySQL/J.

### Usage

Reflection and resource configuration is automatic. Otherwise, during a `native-image` build:

```
native-image ... --features=com.planetscale.PlanetscaleFeature
```

### Installation

**Coordinates:**
```
com.planetscale:planetscale-graalvm
```

**Maven:**
```xml
<dependency>
    <groupId>com.planetscale</groupId>
    <artifactId>planetscale-graalvm</artifactId>
    <version><!-- use the latest version --></version>
</dependency>
```

**Gradle (Version Catalog):**
```kotlin
dependencies {
    implementation(planetscale.integration.graalvm)
}
```

## Library Hygiene

This library is built with **Java 21**, targeting **Java 17** as a minimum baseline. If you need an earlier version of
Java, let us know, and we can probably ship one for you (within reason).

The following devops features are baked into the library's code or process:

- **CI/CD:** Built, tested, and shipped from a cleanroom environment in Github Actions. CI runners are [monitored][7]
  for security and compliance and updated regularly.

- **Testing:** The library has reasonable [test coverage][8], and can be mocked under the hood with [H2][9].

- **Integrity:** Dependencies are locked, hash-sealed and signature-checked. We also ship [SBOMs][15] with each release.

- **Provenance:** [SLSA provenance][10] is issued during the release process, and releases are published to
  [Sigstore][11]. Of course, releases are also signed with [PGP][12].

- **API checks:** Each release of the library is stamped with API layout information, so that library consumer breakage
  can be checked and enforced.

- **Dependencies:** Depends on [MySQL/J][3] and [Kotlin][6]. Each integration library may have other dependencies, like
  [Micronaut][5] or [GraalVM][4].

- **Versioning:** Uses [Semantic Versioning](https://semver.org/).

- **Contributions:** Uses [Developer Certificate of Origin][13] and [Contributor License Agreement][14] to ensure
  contributions are properly licensed.

- **Humans:** We are friendly people who are open to contributions, and we adopt a
  [Code of Conduct](./CODE_OF_CONDUCT.md) which encourages good faith collaboration.

- **License:** TBD, open source.


Made with ❤️ by [Elide](https://elide.dev)

[1]: https://planetscale.com/
[2]: https://vitess.io/
[3]: https://dev.mysql.com/downloads/connector/j/
[4]: https://www.graalvm.org/
[5]: https://micronaut.io/
[6]: https://kotlinlang.org/
[7]: https://app.stepsecurity.io
[8]: https://app.codecov.io/gh/elide-dev/planetscale-java
[9]: https://www.h2database.com/html/main.html
[10]: https://slsa.dev/
[11]: https://sigstore.dev/
[12]: https://www.gnupg.org/
[13]: https://developercertificate.org/
[14]: ./.github/CLA.md
[15]: https://spdx.dev/
