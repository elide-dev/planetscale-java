@file:Suppress(
    "UnstableApiUsage",
    "DSL_SCOPE_VIOLATION",
)

import build.less.plugin.settings.buildless

pluginManagement {
    repositories {
        maven("https://gradle.pkg.st/")
        maven("https://maven.pkg.st/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("build.less") version "1.0.0-beta1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

val micronautVersion: String? by settings
val implementations = listOf(
    "mysqlj",
    "h2",  // for testing
)

val integrations = listOf(
    "graalvm",
    "micronaut",
)

buildless {
    // nothing at this time
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
    versionCatalogs {
        create("mn") {
            from("io.micronaut.platform:micronaut-platform:$micronautVersion")
        }
    }
}

rootProject.name = "planetscale-connector-j"

fun subproject(name: String) {
    include(":subprojects:$name")
}

listOf(
    "core-api"
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
