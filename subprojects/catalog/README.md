
## Planetscale Version Catalog

This directory provides a Gradle publishable [Version Catalog][1] for the Planetscale libraries provided by the
other subprojects. Version mappings are provided for Planetscale and MySQL. Consult the table below for available
libraries.

### Usage

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    versionCatalogs {
        create("planetscale") {
            from("com.planetscale:planetscale-catalog:(version)")
        }
    }
}

// build.gradle.kts
dependencies {
    implementation(planetscale.driver)
    // etc...
}
```

### Libraries

See below for libraries provided by the catalog; no plugins are provided at this time.

| Symbol                  | Coordinates                             | Description                    |
|-------------------------|-----------------------------------------|--------------------------------|
| `driver`                | `com.planetscale:planetscale-driver`    | Main JDBC driver meta-library. |
| `mysql`                 | `com.planetscale:planetscale-mysql`     | MySQL JDBC driver.             |
| `integration.graalvm`   | `com.planetscale:planetscale-graalvm`   | GraalVM integration library.   |
| `integration.kotlin`    | `com.planetscale:planetscale-kotlin`    | Kotlin integration library.    |
| `integration.micronaut` | `com.planetscale:planetscale-micronaut` | Micronaut integration library. |

[1]: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalogs
