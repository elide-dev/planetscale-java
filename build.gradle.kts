import java.nio.charset.StandardCharsets

val stamp: String by properties

group = "com.planetscale.jvm"
version = if (stamp == "true") {
    // if instructed to "stamp" the output libs, include the output from `.version`
    layout.projectDirectory.file(".version").asFile.readText(StandardCharsets.UTF_8).trim()
} else {
    "1.0-SNAPSHOT"
}
