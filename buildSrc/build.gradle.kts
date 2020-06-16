/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()

    maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

val multiplatform = "0.6.1"
val kotlin = "1.3.72"
val gradle = "4.0.0"
val maven_version = "2.1"
val sqldelight_version = "1.2.2"
val google_services = "4.3.3"
val rust_plugin = "0.8.3"

dependencies {
    implementation("dev.icerock:mobile-multiplatform:$multiplatform")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
    implementation("com.android.tools.build:gradle:$gradle")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlin")
    implementation("com.github.dcendents:android-maven-gradle-plugin:$maven_version")
    implementation("com.squareup.sqldelight:gradle-plugin:$sqldelight_version")
    implementation("com.google.gms:google-services:$google_services")
    implementation("gradle.plugin.org.mozilla.rust-android-gradle:plugin:$rust_plugin")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
