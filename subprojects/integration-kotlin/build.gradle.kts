import PlanetscaleBuild.publishable

plugins {
    id("common-conventions.kotlin")
    id("planetscale-publishable.klib")
    id("planetscale-connector.klib")
    alias(libs.plugins.ksp)
}

dependencies {
    api(projects.subprojects.coreApi)
    testImplementation(testFixtures(projects.subprojects.coreApi))
}

kotlin {
    compilerOptions.moduleName = "planetscale.kotlin"
}

publishable(
    name = PlanetscaleBuild.Library.Integrations.KOTLIN,
    description = "Planetscale/Kotlin integration library",
)
