plugins {
    id("planetscale-connector.klib")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
    alias(libs.plugins.micronaut.library)
    alias(libs.plugins.sonar)
    alias(libs.plugins.testLogger)
}

dependencies {
    api(projects.subprojects.coreApi)
}

kotlin {
    compilerOptions.moduleName = "planetscale.graalvm"
}
