import PlanetscaleBuild.publishable

plugins {
    id("planetscale-publishable.klib")
    id(libs.plugins.sonar.get().pluginId)
    `version-catalog`
}

group = "com.planetscale"

catalog {
    versionCatalog {
        version("planetscale", version as String)
        version("mysql", requireNotNull(libs.versions.mysql.get()))

        library("driver", "com.planetscale", PlanetscaleBuild.Library.DRIVER)
            .versionRef("planetscale")
        library("core-api", "com.planetscale", PlanetscaleBuild.Library.CORE_API)
            .versionRef("planetscale")
        library("integration-kotlin", "com.planetscale", PlanetscaleBuild.Library.Integrations.KOTLIN)
            .versionRef("planetscale")
        library("integration-graalvm", "com.planetscale", PlanetscaleBuild.Library.Integrations.GRAALVM)
            .versionRef("planetscale")
        library("integration-micronaut", "com.planetscale", PlanetscaleBuild.Library.Integrations.MICRONAUT)
            .versionRef("planetscale")

        library("mysql", requireNotNull(libs.mysql.get().group), requireNotNull(libs.mysql.get().name))
            .versionRef("mysql")

        bundle("planetscale", listOf(
            "driver",
        ))
    }
}

sonar {
    isSkipProject = true
}

publishable(
    name = PlanetscaleBuild.Library.CATALOG,
    description = "Gradle Version Catalog for Planetscale libraries",
    publicationName = "catalog",
    component = "versionCatalog",
)
