plugins {
    id("planetscale-connector.klib")
    alias(libs.plugins.sonar)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kover)
}

dependencies {
    api(projects.subprojects.coreApi)
    implementation(libs.bundles.mysql)
}

kotlin {
    compilerOptions.moduleName = "planetscale.connector.mysqlj"
}
