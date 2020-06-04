object FilemanagerVersions {
    object Common {
        val appId = "com.merseyside.template"
    }

    object Android {
        const val compileSdk = 28
        const val targetSdk = 29
        const val minSdk = 21

        const val version = "1.0.0"
        const val versionCode = 100
    }

    const val kotlin = "1.3.72"

    object Plugins {
        const val kotlin = FilemanagerVersions.kotlin
        const val serialization = FilemanagerVersions.kotlin
        const val androidExtensions = FilemanagerVersions.kotlin
    }

    object Libs {

        const val kotlinStdLib = FilemanagerVersions.kotlin
        const val coroutines = "1.3.7"
        const val appCompat = "1.1.0"
        const val material = "1.2.0-alpha05"
        const val fragment = "1.2.4"
        const val constraintLayout = "1.1.3"
        const val lifecycle = "2.0.0"
        const val cardView = "1.0.0"
        const val recyclerView = "1.0.0"

        const val zip4j = "2.6.0"

    }
}