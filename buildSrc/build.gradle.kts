plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()

    maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

val multiplatform = "0.8.0"
val kotlin = "1.4.21"
val gradle = "4.1.0"
val mavenVersion = "2.1"
val sqldelightVersion = "1.4.4"
val googleServices = "4.3.3"
val rustPlugin = "0.8.3"
val resources = "0.13.1"
val buildKonfig = "0.3.3"

dependencies {
    implementation("dev.icerock:mobile-multiplatform:$multiplatform")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
    implementation("com.android.tools.build:gradle:$gradle")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlin")
    implementation("com.github.dcendents:android-maven-gradle-plugin:$mavenVersion")
    implementation("com.squareup.sqldelight:gradle-plugin:$sqldelightVersion")
    implementation("com.google.gms:google-services:$googleServices")
    implementation("dev.icerock.moko:resources-generator:$resources")
    implementation("gradle.plugin.org.mozilla.rust-android-gradle:plugin:$rustPlugin")
    implementation("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:$buildKonfig")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
