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
}

val stamp: String by properties

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

    spotless {
        ratchetFrom("origin/main")

        kotlin {
            target("**/*.kt")
            ktlint(libs.versions.ktlint.get())
        }
    }

    sonar {
        properties {
            property("sonar.sources", "src")
        }
    }
}

apiValidation {
    // Projects which are not published or otherwise do not expose a JVM API.
    ignoredProjects.addAll(listOf(
        "catalog",
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
        xml {
            setReportFile(layout.buildDirectory.file("reports/kover/merged/xml/report.xml"))
        }
    }
}
