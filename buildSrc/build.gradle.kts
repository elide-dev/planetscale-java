plugins {
    `kotlin-dsl`
}

val kotlinVersion = "1.9.20"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation(libs.plugin.nexus.publish)
    implementation(libs.plugin.sigstore)
}
