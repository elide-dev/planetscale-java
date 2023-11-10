//
// Planetscale/J
//

import java.nio.charset.StandardCharsets
import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    alias(libs.plugins.doctor)
    alias(libs.plugins.sbom)
    alias(libs.plugins.sonar)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinx.plugin.abiValidator)
    alias(libs.plugins.version.check)
    alias(libs.plugins.version.catalogUpdate)
    alias(libs.plugins.spotless)
    alias(libs.plugins.testLogger)

    id(libs.plugins.sigstore.get().pluginId)
    id(libs.plugins.nexus.publish.get().pluginId)
    `project-report`
    idea
}

val stamp: String by properties
val coverageReportPath = "reports/kover/merged/xml/report.xml"

group = PlanetscaleBuild.Library.GROUP
version = if (stamp == "true") {
    // if instructed to "stamp" the output libs, include the output from `.version`
    layout.projectDirectory.file(".version").asFile.readText(StandardCharsets.UTF_8).trim()
} else {
    "1.0-SNAPSHOT"
}

val spotlessPlugin = libs.plugins.spotless.get().pluginId
val sonarPlugin = libs.plugins.sonar.get().pluginId
val koverPlugin = libs.plugins.kover.get().pluginId
val testloggerPlugin = libs.plugins.testLogger.get().pluginId
val sbomPlugin = libs.plugins.sbom.get().pluginId

sonar {
    properties {
        property("sonar.projectKey", "elide-dev_planetscale-java")
        property("sonar.organization", "elide-dev")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.gradle.skipCompile", "true")
        property("sonar.coverage.jacoco.xmlReportPaths", listOf(
            coverageReportPath,
            "build/reports/kover/report.xml",
        ).joinToString(","))
    }
}

subprojects {
    apply(plugin = spotlessPlugin)
    apply(plugin = sonarPlugin)
    apply(plugin = koverPlugin)
    apply(plugin = testloggerPlugin)
    apply(plugin = sbomPlugin)

    testlogger {
        theme = ThemeType.MOCHA
    }

    koverReport {
        defaults {
            xml {
                onCheck = true
                setReportFile(layout.buildDirectory.file(coverageReportPath))
            }

            verify {
                onCheck = true
            }
        }
    }

    spotless {
        ratchetFrom("origin/main")

        kotlin {
            target("**/*.kt")
            targetExclude("**/test/**/*.kt")
            ktlint(libs.versions.ktlint.get())
        }
    }

    sonar {
        properties {
            property("sonar.sources", "src/main/jvm")
            property("sonar.tests", "src/test/kotlin")
            property("sonar.java.binaries", "build/classes")
        }
    }

    tasks.check.configure {
        dependsOn(listOfNotNull(
            tasks.findByName("test"),
            tasks.findByName("koverXmlReport"),
            tasks.findByName("spotlessKotlinCheck"),
        ))
    }
}

apiValidation {
    // Projects which are not published or otherwise do not expose a JVM API.
    ignoredProjects.addAll(listOf(
        "catalog",
        "driver",
        "impl-h2",
    ))
}

dependencies {
    kover(projects.subprojects.coreApi)
    kover(projects.subprojects.driver)
    kover(projects.subprojects.implMysqlj)
    kover(projects.subprojects.integrationGraalvm)
    kover(projects.subprojects.integrationKotlin)
    kover(projects.subprojects.integrationMicronaut)
}

koverReport {
    defaults {
        html {
            onCheck = true
        }

        xml {
            onCheck = true
            setReportFile(layout.buildDirectory.file(coverageReportPath))
        }

        verify {
            onCheck = true
        }
    }
}

val preMerge by tasks.registering {
    group = "build"
    description = "Per-merge build, test, and check"

    dependsOn(listOfNotNull(
        tasks.build,
        tasks.check,
        tasks.sonar,
        tasks.findByName("apiCheck"),
    ))
}
