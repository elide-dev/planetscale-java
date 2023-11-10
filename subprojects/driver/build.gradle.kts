import PlanetscaleBuild.publishable

plugins {
    id("common-conventions.kotlin")
    id("planetscale-publishable.klib")
    id(libs.plugins.sonar.get().pluginId)
}

dependencies {
    api(projects.subprojects.coreApi)
    implementation(projects.subprojects.implMysqlj)
    testImplementation(libs.bundles.junit5)
    testImplementation(testFixtures(projects.subprojects.coreApi))
}

kotlin {
    compilerOptions.moduleName = "planetscale.driver"
}

publishable(
    name = PlanetscaleBuild.Library.DRIVER,
    description = "Planetscale JDBC meta-driver",
)
