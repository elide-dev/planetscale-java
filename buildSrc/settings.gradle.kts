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
    id("build.less")
    id("org.gradle.toolchains.foojay-resolver-convention")
}

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
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "planetscale-buildsrc"
