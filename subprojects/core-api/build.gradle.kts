import PlanetscaleBuild.publishable

plugins {
    id("planetscale-connector.klib")
    id("planetscale-publishable.klib")
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.graalvm)
    `java-test-fixtures`
    idea
}

buildConfig {
    className("BuildConstants")
    packageName("com.planetscale.jvm.driver")
    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = true
    }

    buildConfigField("String", "VERSION", "\"${version as String}\"")
    buildConfigField("String", "DEPENDENCY_MYSQL", "\"${libs.versions.mysql.get()}\"")
}

dependencies {
    api(libs.bundles.slf4j)
    api(kotlin("stdlib"))

    testImplementation(libs.bundles.junit5)
    testImplementation(libs.bundles.logback)
    testImplementation(projects.subprojects.implH2)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testFixturesApi(kotlin("test"))
    testFixturesApi(kotlin("test-junit5"))
    testFixturesApi(libs.bundles.junit5)
}

kotlin {
    compilerOptions.moduleName = "planetscale.connector"
}

publishable(
    name = PlanetscaleBuild.Library.CORE_API,
    description = "Planetscale driver JDBC interfaces",
)
