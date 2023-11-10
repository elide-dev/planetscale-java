import PlanetscaleBuild.publishable

plugins {
    id("common-conventions.kotlin")
    id("planetscale-publishable.klib")
    id("planetscale-connector.klib")
    alias(libs.plugins.micronaut.library)
}

dependencies {
    api(projects.subprojects.coreApi)
    api(libs.graalvm.sdk)
    testImplementation(testFixtures(projects.subprojects.coreApi))
}

kotlin {
    compilerOptions.moduleName = "planetscale.graalvm"
}

publishable(
    name = PlanetscaleBuild.Library.Integrations.GRAALVM,
    description = "Planetscale/GraalVM integration library",
)
