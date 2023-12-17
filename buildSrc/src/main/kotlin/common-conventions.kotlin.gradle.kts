@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

val javaToolchainVersion = JavaVersion.VERSION_21
val javaTarget = JavaVersion.VERSION_17
val kotlinJavaTarget = JvmTarget.JVM_17
val kotlinVersion = KotlinVersion.KOTLIN_1_9
val lockDeps: String? by properties
val kotlinLangVersion: String? by properties
val kotlinSdkVersion = properties["kotlin.version"] as String?
val defaultKotlinVersion = "1.9"
val defaultKotlinSdkVersion = "1.9.21"
val kotlinCompilerArgs = listOf<String>()

val forcedResolutions = listOf(
    // https://github.com/elide-dev/planetscale-java/security/dependabot/4
    "org.json:json" to ("20231013" to "elide-dev/planetscale-java/security/dependabot/4"),
    // https://github.com/elide-dev/planetscale-java/security/dependabot/3
    "com.google.guava:guava" to ("32.1.3-jre" to "elide-dev/planetscale-java/security/dependabot/3"),
    // https://github.com/elide-dev/planetscale-java/security/dependabot/2
    "com.squareup.okio:okio" to ("3.4.0" to "elide-dev/planetscale-java/security/dependabot/2"),
    // https://github.com/elide-dev/planetscale-java/security/dependabot/2
    "com.squareup.okio:okio-jvm" to ("3.4.0" to "elide-dev/planetscale-java/security/dependabot/2"),
    // kotlin stdlib is pinned to avoid incompatibilities
    "org.jetbrains.kotlin" to ((kotlinSdkVersion ?: defaultKotlinSdkVersion) to "pinned kotlin stdlib"),
).toMap().toSortedMap()

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
        api,
        implementation,
        testApi,
        testImplementation,
        compileClasspath,
        runtimeClasspath,
        testCompileClasspath,
        testRuntimeClasspath,
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
        if (forcedResolutions.containsKey("${requested.group}:${requested.name}")) {
            val (version, reason) = requireNotNull(forcedResolutions["${requested.group}:${requested.name}"])
            useVersion(version)
            because("pin ${requested.group}:${requested.name} for security reasons: $reason")
        }
    }
}

afterEvaluate {
    val compileKotlin: KotlinCompile by tasks
    val compileJava: JavaCompile by tasks
    compileKotlin.destinationDirectory.set(compileJava.destinationDirectory)

    tasks.compileJava.configure {
        dependsOn(compileKotlin)
        mustRunAfter(compileKotlin)
    }
    tasks.compileKotlin.configure {
        destinationDirectory = compileJava.destinationDirectory
    }
}
