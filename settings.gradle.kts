@file:Suppress(
    "UnstableApiUsage",
    "DSL_SCOPE_VIOLATION",
)

pluginManagement {
    repositories {
        maven("https://gradle.pkg.st/")
        maven("https://maven.pkg.st/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("build.less") version "1.0.0-rc2"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"

    id("com.gradle.enterprise") version("3.15.1")
    id("com.gradle.common-custom-user-data-gradle-plugin") version("1.12")
    id("io.micronaut.platform.catalog") version(extra.properties["micronautVersion"] as String)
}

val implementations = listOf(
    "mysqlj",
    "h2",  // for testing
)

val integrations = listOf(
    "graalvm",
    "kotlin",
    "micronaut",
)

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(
        RepositoriesMode.FAIL_ON_PROJECT_REPOS
    )
    repositories {
        maven("https://gradle.pkg.st/")
        maven("https://maven.pkg.st/")
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "planetscale-connector-j"

fun subproject(name: String) {
    include(":subprojects:$name")
}

listOf(
    "catalog",
    "core-api",
    "driver",
).plus(implementations.map {
    "impl-$it"
}).plus(integrations.map {
    "integration-$it"
}).forEach(
    ::subproject
)

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("GROOVY_COMPILATION_AVOIDANCE")
