import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    id("planetscale-connector.klib")
    alias(libs.plugins.ksp)
    alias(libs.plugins.sonar)
    alias(libs.plugins.testLogger)
    alias(libs.plugins.kover)
}

testlogger {
    theme = ThemeType.MOCHA
}

dependencies {
    api(libs.bundles.slf4j)
    api(kotlin("stdlib"))

    testImplementation(libs.bundles.junit5)
    testImplementation(libs.bundles.logback)
    testImplementation(projects.subprojects.implH2)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

kotlin {
    compilerOptions.moduleName = "planetscale.connector"
}
