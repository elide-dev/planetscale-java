@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

val javaToolchainVersion = JavaVersion.VERSION_20
val javaTarget = JavaVersion.VERSION_17
val kotlinJavaTarget = JvmTarget.JVM_17
val kotlinVersion = KotlinVersion.KOTLIN_2_0
val lockDeps: String? by properties
val kotlinLangVersion: String? by properties
val kotlinSdkVersion = properties["kotlin.version"] as String?
val defaultKotlinVersion = "2.0"
val defaultKotlinSdkVersion = "1.9.20"
val kotlinCompilerArgs = listOf<String>()

group = PlanetscaleBuild.Library.GROUP

sourceSets {
    main {
        java {
            srcDirs(layout.projectDirectory.dir("src/main/jvm"))
        }
        kotlin {
            srcDirs(layout.projectDirectory.dir("src/main/jvm"))
        }
        resources {
            srcDirs(layout.projectDirectory.dir("src/main/resources"))
        }
    }
}

java {
    // include ancillary jars
    withSourcesJar()

    // set target jvm version and enable modularity
    sourceCompatibility = javaTarget
    targetCompatibility = javaTarget
    modularity.inferModulePath = true

    // build against jvm version for tooling
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaToolchainVersion.majorVersion)
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Warning

    sourceSets.all {
        languageSettings.progressiveMode = true
        languageSettings.languageVersion = kotlinLangVersion ?: defaultKotlinVersion
        languageSettings.apiVersion = kotlinLangVersion ?: defaultKotlinVersion
    }

    compilerOptions {
        apiVersion = kotlinVersion
        languageVersion = kotlinVersion
        javaParameters = true
        progressiveMode = true
        jvmTarget = kotlinJavaTarget
        freeCompilerArgs = kotlinCompilerArgs.plus(freeCompilerArgs.get())
    }
}

listOf(
    Zip::class,
    Tar::class,
    Jar::class,
).forEach {
    tasks.withType(it).configureEach {
        isReproducibleFileOrder = true
        isPreserveFileTimestamps = false
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

configurations.all {
    resolutionStrategy {
        // fail eagerly on version conflict (includes transitive dependencies)
        failOnVersionConflict()

        // prefer modules that are part of this build
        preferProjectModules()

        // fail if non-reproducible
        if (lockDeps != "true") failOnNonReproducibleResolution()

        // fail on dynamic versions if lock deps is disabled
        if (lockDeps != "true") failOnDynamicVersions()
    }
}

if (lockDeps == "true") configurations {
    listOf(
        compileClasspath,
        runtimeClasspath,
    ).forEach {
        it.configure {
            resolutionStrategy.activateDependencyLocking()
            resolutionStrategy.enableDependencyVerification()
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.compileJava {
    options.encoding = "UTF-8"
    modularity.inferModulePath = true
}

tasks.test {
    useJUnitPlatform()
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin" && requested.name.contains("stdlib")) {
            useVersion(kotlinSdkVersion ?: defaultKotlinSdkVersion)
            because("pin kotlin stdlib")
        }
    }
}

afterEvaluate {
    val compileKotlin: KotlinCompile by tasks
    val compileJava: JavaCompile by tasks
    compileKotlin.destinationDirectory.set(compileJava.destinationDirectory)
}
