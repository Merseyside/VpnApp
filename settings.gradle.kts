enableFeaturePreview("GRADLE_METADATA")

pluginManagement {
    repositories {
        jcenter()
        google()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://jetbrains.bintray.com/kotlin-native-dependencies") }
        maven { url = uri("https://maven.fabric.io/public") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    resolutionStrategy.eachPlugin {
        // part of plugins defined in Deps.Plugins, part in buildSrc/build.gradle.kts
        val module = Deps.plugins[requested.id.id] ?: return@eachPlugin

        useModule(module)
    }
}

include(":app")
include(":shared")
include(":openvpn-core")

include(":filemanager")
project(":filemanager").projectDir =
    File(settingsDir, "../android-filemanager/filemanager")

if (Modules.isLocalDependencies) {

    include(LibraryModules.MultiPlatform.cleanMvvmArch.name)
    project(LibraryModules.MultiPlatform.cleanMvvmArch.name).projectDir =
        File(settingsDir, "../mersey-library/kmp-clean-mvvm-arch")

    include(LibraryModules.MultiPlatform.utils.name)
    project(LibraryModules.MultiPlatform.utils.name).projectDir =
        File(settingsDir, "../mersey-library/kmp-utils")

    include(LibraryModules.Android.animators)
    project(LibraryModules.Android.animators).projectDir =
        File(settingsDir, "../mersey-library/animators")

    include(LibraryModules.Android.cleanMvvmArch)
    project(LibraryModules.Android.cleanMvvmArch).projectDir =
        File(settingsDir, "../mersey-library/clean-mvvm-arch")

    include(LibraryModules.Android.adapters)
    project(LibraryModules.Android.adapters).projectDir =
        File(settingsDir, "../mersey-library/adapters")

    include(LibraryModules.Android.utils)
    project(LibraryModules.Android.utils).projectDir =
        File(settingsDir, "../mersey-library/utils")
}

rootProject.name = "vpn-application-android"