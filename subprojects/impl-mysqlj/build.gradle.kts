import PlanetscaleBuild.publishable

plugins {
    id("common-conventions.kotlin")
    id("planetscale-publishable.klib")
    id("planetscale-connector.klib")
}

dependencies {
    api(projects.subprojects.coreApi)
    implementation(libs.bundles.mysql)
}

kotlin {
    compilerOptions.moduleName = "planetscale.connector.mysqlj"
}

publishable(
    name = PlanetscaleBuild.Library.Implementations.MYSQLJ,
    description = "Planetscale driver MySQL/J implementation (internal)",
)
