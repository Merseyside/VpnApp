plugins {
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinAndroidExtensions)
    plugin(Deps.Plugins.kotlinSerialization)
    plugin(Deps.Plugins.sqlDelight)
    plugin(Deps.Plugins.mobileMultiplatform)
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }

    androidExtensions {
        isExperimental = true
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
}

configurations {
    all {
        exclude(group = "org.apache.xmlgraphics", module = "batik-ext")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val mppLibs = listOf(
    Deps.Libs.MultiPlatform.kotlinStdLib,
    Deps.Libs.MultiPlatform.coroutines,
    Deps.Libs.MultiPlatform.serialization,
    Deps.Libs.MultiPlatform.ktorClient,
    Deps.Libs.MultiPlatform.ktorClientLogging,
    Deps.Libs.MultiPlatform.kodein,
    Deps.Libs.MultiPlatform.kodeinErased,
    Deps.Libs.MultiPlatform.sqlDelight,
    Deps.Libs.MultiPlatform.klock,
    Deps.Libs.MultiPlatform.preferences,
    Deps.Libs.MultiPlatform.settings
)


val androidLibs = listOf(
    Deps.Libs.Android.lifecycle,
    Deps.Libs.Android.sshj,
    Deps.Libs.Android.json,
    Deps.Libs.Android.okhttp,
    Deps.Libs.Android.okhttpInterceptor,
    Deps.Libs.Android.ktorOkHttp,
    Deps.Libs.Android.wireguard
    //Deps.Libs.Android.filemanager
)

val localMerseyModules = listOf(
    LibraryModules.MultiPlatform.cleanMvvmArch,
    LibraryModules.MultiPlatform.utils
)

val merseyModules = listOf<MultiPlatformLibrary>(

)

val modulez = listOf(
    Modules.Android.filemanager,
    Modules.Android.openvpn
)

setupFramework(
    exports = mppLibs
)

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.hierynomus:sshj:0.27.0")

    api(project(Modules.Android.shadowsocks))

    modulez.forEach { module -> implementation(project(module))}
    androidLibs.forEach { lib -> androidLibrary(lib)}
    mppLibs.forEach { mppLibrary(it) }

    if (Modules.isLocalDependencies) {
        localMerseyModules.forEach { mppModule(it) }
    } else {
        merseyModules.forEach { library -> mppLibrary(library) }
    }
}

sqldelight {
    database("VpnDatabase") {
        packageName = "com.merseyside.dropletapp.data.db"
        sourceFolders = listOf("sqldelight")
        schemaOutputDirectory = file("build/dbs")
        //dependency(project(":OtherProject"))
    }
    linkSqlite = false
}
repositories {
    mavenCentral()
}