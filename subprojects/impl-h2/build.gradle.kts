plugins {
    id("planetscale-connector.klib")
}

dependencies {
    api(projects.subprojects.coreApi)
    implementation(libs.bundles.h2)
}

kotlin {
    compilerOptions.moduleName = "planetscale.connector.inmemory"
}
