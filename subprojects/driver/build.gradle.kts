import PlanetscaleBuild.publishable

plugins {
    id("common-conventions.kotlin")
    id("planetscale-publishable.klib")
    alias(libs.plugins.ksp)
    id(libs.plugins.sonar.get().pluginId)
}

dependencies {
    api(libs.bundles.slf4j)
    api(kotlin("stdlib"))
    api(projects.subprojects.coreApi)
    api(libs.bundles.mysql)
    implementation(projects.subprojects.implMysqlj)

    testImplementation(libs.bundles.junit5)
    testImplementation(testFixtures(projects.subprojects.coreApi))
    testRuntimeOnly(projects.subprojects.implH2)
    testApi(libs.bundles.h2)
}

kotlin {
    compilerOptions.moduleName = "planetscale.driver"
}

publishable(
    name = PlanetscaleBuild.Library.DRIVER,
    description = "Planetscale JDBC meta-driver",
)

tasks.test.configure {
    jvmArgs("-Dplanetscale.driver=com.planetscale.jvm.h2.PlanetscaleH2Driver")
}
