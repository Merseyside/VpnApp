enableFeaturePreview("GRADLE_METADATA")

include(":app")
include(":shared")
include("openvpn-core")

include(":plugin")
project(":plugin").projectDir =
    File(rootDir.parent, "shadowsocks-android/plugin")

include(":core")
project(":core").projectDir =
    File(rootDir.parent, "shadowsocks-android/core")

include(":filemanager")
project(":filemanager").projectDir =
    File(rootDir.parent, "android-filemanager/filemanager")

val isLocalDependencies = true

if (isLocalDependencies) {

    include(":kmp-clean-mvvm-arch")
    project(":kmp-clean-mvvm-arch").projectDir =
        File(rootDir.parent, "mersey-android-library/kmp-clean-mvvm-arch")

    include(":kmp-utils")
    project(":kmp-utils").projectDir =
        File(rootDir.parent, "mersey-android-library/kmp-utils")

    include(":utils")
    project(":utils").projectDir =
        File(rootDir.parent, "mersey-android-library/utils")

    include(":animators")
    project(":animators").projectDir =
        File(rootDir.parent, "mersey-android-library/animators")

    include(":clean-mvvm-arch")
    project(":clean-mvvm-arch").projectDir =
        File(rootDir.parent, "mersey-android-library/clean-mvvm-arch")

    include(":adapters")
    project(":adapters").projectDir =
        File(rootDir.parent, "mersey-android-library/adapters")

//    include(LibraryModules.Android.animators)
//    project(LibraryModules.Android.animators).projectDir =
//        File(rootDir.parent, "${Modules.library}/animators")
//
//    include(LibraryModules.Android.cleanMvvmArch)
//    project(LibraryModules.Android.cleanMvvmArch).projectDir =
//        File(rootDir.parent, "${Modules.library}/clean-mvvm-arch")
//
//    include(LibraryModules.Android.adapters)
//    project(LibraryModules.Android.adapters).projectDir =
//        File(rootDir.parent, "${Modules.library}/adapters")
//
//    include(LibraryModules.Android.utils)
//    project(LibraryModules.Android.utils).projectDir =
//        File(rootDir.parent, "${Modules.library}/utils")
}

rootProject.name = "vpn-application-android"