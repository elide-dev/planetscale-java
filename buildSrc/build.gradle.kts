plugins {
    `kotlin-dsl`
}

val kotlinVersion = libs.versions.kotlin.sdk.get()

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation(libs.plugin.nexus.publish)
    implementation(libs.plugin.sigstore)
}
