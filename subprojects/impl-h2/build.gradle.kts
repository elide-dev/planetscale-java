import PlanetscaleBuild.baseline

plugins {
    id("common-conventions.kotlin")
    id("planetscale-connector.klib")
}

// Baseline (non-publishable) project settings
baseline()

dependencies {
    api(projects.subprojects.coreApi)
    implementation(libs.bundles.h2)
}

kotlin {
    compilerOptions.moduleName = "planetscale.connector.inmemory"
}
