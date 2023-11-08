import dev.sigstore.sign.SigstoreSignExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension

/** Register the "javadocJar" task and include it in maven publications. */
internal fun Project.publishJavadocJar() {
    extensions.findByType(PublishingExtension::class.java)?.apply {
        publications.withType(MavenPublication::class.java) {
            artifact(tasks.named("javadocJar"))
        }
    }
}

/** Include the source task in publications. */
internal fun Project.publishSourcesJar() {
    extensions.findByType(PublishingExtension::class.java)?.apply {
        publications.withType(MavenPublication::class.java) {
            artifact(tasks.named("sourcesJar"))
        }
    }
}

/** Configure signing for all of this project's publications and archive tasks. */
internal fun Project.configureSigning() {
    // resolve the publishing extension (so we can sign publications)
    val publishing = extensions.getByType(PublishingExtension::class.java)

    extensions.getByType(SigningExtension::class.java).apply {
        if (findProperty("enableSigning").toString().toBoolean()) {
            // sign all archives (JAR and ZIP files)
            sign(configurations.getByName("archives"))

            // sign all publications
            sign(publishing.publications)
        }
    }

    // configure publishing tasks to depend on signing
    val signingTasks = tasks.withType(Sign::class.java)
    tasks.withType(AbstractPublishToMaven::class.java).configureEach {
        dependsOn(signingTasks)
    }
}

internal fun Project.configureSigstore() {
    extensions.getByType(SigstoreSignExtension::class.java).apply {
        oidcClient.apply {
            gitHub {
                audience.set("sigstore")
            }
            web {
                clientId.set("sigstore")
                issuer.set("https://oauth2.sigstore.dev/auth")
            }
        }
    }
}
