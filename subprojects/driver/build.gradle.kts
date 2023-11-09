import PlanetscaleBuild.publishable

plugins {
    id("common-conventions.kotlin")
    id("planetscale-publishable.klib")
    id(libs.plugins.sonar.get().pluginId)
}

dependencies {
    api(projects.subprojects.coreApi)
    implementation(projects.subprojects.implMysqlj)
}

publishable(
    name = PlanetscaleBuild.Library.DRIVER,
    description = "Planetscale JDBC meta-driver",
)

sonar {
    isSkipProject = true
}
