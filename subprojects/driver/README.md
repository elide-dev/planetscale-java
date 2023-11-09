
## Planetscale/J: Driver

This module defines a POM-only publication which gathers the appropriate dependencies for the Planetscale/J
JDBC driver. To use this library, install it via the [Version Catalog][1], or via any of the following samples:


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

### Integration Libraries

See the [main README][2] for ancillary integration libraries.

[1]: ../catalog
[2]: ../README.md
