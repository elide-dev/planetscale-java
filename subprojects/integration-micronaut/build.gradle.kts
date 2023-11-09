import PlanetscaleBuild.publishable

plugins {
    id("common-conventions.kotlin")
    id("planetscale-publishable.klib")
    id("planetscale-connector.klib")
    alias(libs.plugins.ksp)
    alias(libs.plugins.micronaut.library)
}

dependencies {
    api(projects.subprojects.coreApi)
    api(mn.micronaut.core)
    ksp(mn.micronaut.inject.kotlin)
}

kotlin {
    compilerOptions.moduleName = "planetscale.micronaut"
}

publishable(
    name = PlanetscaleBuild.Library.Integrations.MICRONAUT,
    description = "Planetscale/Micronaut integration library",
)
