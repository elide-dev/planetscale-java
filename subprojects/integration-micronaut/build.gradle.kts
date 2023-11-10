import PlanetscaleBuild.publishable
import io.micronaut.gradle.MicronautTestRuntime

plugins {
    id("common-conventions.kotlin")
    id("planetscale-publishable.klib")
    id("planetscale-connector.klib")
    alias(libs.plugins.ksp)
    alias(libs.plugins.micronaut.library)
}

micronaut {
    testRuntime = MicronautTestRuntime.JUNIT_5
}

dependencies {
    annotationProcessor(mn.micronaut.inject.java)

    api(mn.micronaut.core)
    api(mn.micronaut.jdbc)
    api(mn.micronaut.data.jdbc)
    api(mn.micronaut.data.connection.jdbc)
    api(projects.subprojects.coreApi)
    api(projects.subprojects.driver)
    implementation(projects.subprojects.implMysqlj)

    ksp(mn.micronaut.inject.kotlin)
    testImplementation(mn.snakeyaml)
    testImplementation(libs.bundles.junit5)
    testImplementation(mn.micronaut.test.junit5)
    testImplementation(testFixtures(projects.subprojects.coreApi))
    testRuntimeOnly(libs.junit.jupiter.engine)
}

kotlin {
    compilerOptions.moduleName = "planetscale.micronaut"
}

publishable(
    name = PlanetscaleBuild.Library.Integrations.MICRONAUT,
    description = "Planetscale/Micronaut integration library",
)
