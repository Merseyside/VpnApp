plugins {
    plugin(LibraryDeps.Plugins.androidApplication)
    plugin(LibraryDeps.Plugins.kotlinAndroid)
    plugin(LibraryDeps.Plugins.kotlinAndroidExtensions)
    plugin(LibraryDeps.Plugins.kotlinSerialization)
    plugin(LibraryDeps.Plugins.kotlinKapt)
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)

        applicationId = Versions.Common.appId

        versionCode = Versions.Android.versionCode
        versionName = Versions.Android.version

        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }

    buildFeatures.dataBinding = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    signingConfigs {
        create("release") {
            keyAlias = project.property("RELEASE_KEY_ALIAS") as String
            keyPassword = project.property("RELEASE_KEY_PASSWORD") as String
            storeFile = file(project.property("RELEASE_STORE_FILE") as String)
            storePassword = project.property("RELEASE_STORE_PASSWORD") as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
    }

    lintOptions {
        isCheckReleaseBuilds = false
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("license/README.dom.txt")
        exclude("license/LICENSE.dom-documentation.txt")
        exclude("license/NOTICE")
        exclude("license/LICENSE.dom-software.txt")
        exclude("license/LICENSE")
    }

    sourceSets.getByName("main") {
        res.srcDir("src/main/res/")
        res.srcDir("src/main/res/layouts/fragment")
        res.srcDir("src/main/res/layouts/activity")
        res.srcDir("src/main/res/layouts/dialog")
        res.srcDir("src/main/res/layouts/views")
        res.srcDir("src/main/res/countries/")
        res.srcDir("src/main/res/connectBars/")
        res.srcDir("src/main/res/easyAccess/")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

configurations {
    all {
        exclude(group = "org.apache.xmlgraphics", module = "batik-ext")
    }
}

val androidLibs = listOf(
    Deps.Libs.Android.kotlinStdLib.name,
    Deps.Libs.Android.appCompat.name,
    Deps.Libs.Android.material.name,
    Deps.Libs.Android.fragment.name,
    Deps.Libs.Android.recyclerView.name,
    Deps.Libs.Android.lifecycle.name,
    Deps.Libs.Android.constraintLayout.name,
    Deps.Libs.MultiPlatform.serialization.android!!,


    Deps.Libs.Android.dagger.name,
    Deps.Libs.Android.preferences.name,
    Deps.Libs.Android.cicerone.name,
    Deps.Libs.Android.selectorView.name,
    Deps.Libs.Android.qrGen.name,
    Deps.Libs.Android.firebaseCore.name,
    Deps.Libs.Android.firebaseConfig.name,
    Deps.Libs.Android.firebaseAnalytics.name,
    Deps.Libs.Android.okhttp.name,
    Deps.Libs.MultiPlatform.sqlDelight.android!!,
    Deps.Libs.Android.billing.name,
    Deps.Libs.Android.billingKtx.name,
    Deps.Libs.Android.mahEncryptor.name
    //Deps.Libs.Android.filemanager.name
)

val localMerseyLibs = listOf(
    LibraryModules.Android.archy,
    LibraryModules.Android.adapters,
    LibraryModules.Android.animators,
    LibraryModules.Android.utils
)

val merseyLibs = listOf(
    Deps.Libs.Android.MerseyLibs.cleanMvvmArch.name,
    Deps.Libs.Android.MerseyLibs.adapters.name,
    Deps.Libs.Android.MerseyLibs.animators.name,
    Deps.Libs.Android.MerseyLibs.utils.name
)

dependencies {
    androidLibs.forEach { lib -> implementation(lib) }

    if (Modules.isLocalDependencies) {
        localMerseyLibs.forEach { lib -> implementation(project(lib)) }
    } else {
        merseyLibs.forEach { lib -> implementation(lib) }
    }

    implementation(project(":shared"))
    implementation(project(Modules.Android.filemanager))
    implementation(project(Modules.Android.openvpn))

    compileOnly("javax.annotation:jsr250-api:1.0")

    kaptLibrary(Deps.Libs.Android.daggerCompiler)
}

apply("plugin" to "com.google.gms.google-services")
repositories {
    mavenCentral()
}