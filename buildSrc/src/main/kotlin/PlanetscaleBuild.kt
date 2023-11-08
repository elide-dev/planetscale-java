import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

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
    }

    fun Project.publishable(name: String, description: String) {
        baseline()
        publishJavadocJar()
        publishSourcesJar()
        configureSigning()
        configureSigstore()

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
            }
        }
    }
}
