@file:Suppress("MemberVisibilityCanBePrivate")

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Zip
import org.gradle.jvm.tasks.Jar

object PlanetscaleBuild {
    object Library {
        const val GROUP = "com.planetscale"
        const val CATALOG = "planetscale-catalog"
        const val CORE_API = "planetscale-core-api"
        const val DRIVER = "planetscale-jvm"

        object Implementations {
            const val MYSQLJ = "planetscale-mysqlj"
        }

        object Integrations {
            const val MICRONAUT = "planetscale-micronaut"
            const val GRAALVM = "planetscale-graalvm"
            const val KOTLIN = "planetscale-kotlin"
        }
    }

    fun Project.baseline() {
        // nothing yet
        project.apply {
            tasks.withType(Jar::class.java).configureEach {
                isReproducibleFileOrder = true
                isPreserveFileTimestamps = false
            }
            tasks.withType(Zip::class.java).configureEach {
                isReproducibleFileOrder = true
                isPreserveFileTimestamps = false
            }
            tasks.withType(Tar::class.java).configureEach {
                isReproducibleFileOrder = true
                isPreserveFileTimestamps = false
            }
        }
    }

    fun Project.publishable(name: String, description: String, action: MavenPublication.() -> Unit = {}) {
        baseline()
        if (project.properties["stamp"] == "true") {
            publishJavadocJar()
            publishSourcesJar()
            configureSigning()
            configureSigstore()
        }

        extensions.getByType(PublishingExtension::class.java).apply {
            publications.withType(MavenPublication::class.java) {
                artifactId = name

                pom {
                    this.name.set(name)
                    this.description.set(description)

                    developers {
                        developer {
                            id.set("sgammon")
                            this.name.set("Sam Gammon")
                            email.set("samuel.gammon@gmail.com")
                        }
                        developer {
                            id.set("darvld")
                            this.name.set("Dario Valdespino")
                        }
                    }
                    scm {
                        url.set("https://github.com/elide-dev/elide")
                    }
                }

                // let consumer customize
                action.invoke(this)
            }
        }
    }
}
