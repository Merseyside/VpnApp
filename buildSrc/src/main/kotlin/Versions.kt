object Versions {
    object Common {
        val appId = "com.merseyside.dropletapp"
    }

    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 21

        const val version = "1.08"
        const val versionCode = 108
    }

    const val kotlin = "1.3.72"

    private const val mokoResources = "0.9.0"

    object Plugins {
        const val android = "4.0.0"

        const val kotlin = LibraryVersions.kotlin
        const val serialization = LibraryVersions.kotlin
        const val androidExtensions = LibraryVersions.kotlin
        const val maven = "2.1"
        const val sqlDelight = "1.2.2"
    }

    object Libs {
        object Android {
            const val kotlinStdLib = Versions.kotlin
            const val coroutines = "1.3.7"
            const val appCompat = "1.1.0"
            const val material = "1.2.0-alpha06"
            const val fragment = "1.2.4"
            const val constraintLayout = "1.1.3"
            const val lifecycle = "2.0.0"
            const val cardView = "1.0.0"
            const val recyclerView = "1.0.0"
            const val dagger = "2.27"
            const val navigation = "2.2.1"
            const val paging = "1.0.1"
            const val billing = "2.2.0"
            const val publisher = "v3-rev142-1.25.0"
            const val auth = "0.20.0"
            const val firebaseFirestore = "21.4.3"
            const val firebaseCore = "17.4.2"
            const val firebaseConfig = "19.1.4"
            const val firebaseAnalytics = "17.4.2"
            const val playCore = "1.7.2"
            const val keyboard = "2.3.0"
            const val gson = "2.8.6"
            const val worker = "2.3.4"
            const val room = "2.0.0"
            const val preferences = "1.1.0"
            const val cicerone = "5.0.0"
            const val selectorView = "1.13"
            const val qrGen = "2.6.0"
            const val okhttp = "4.4.1"
            const val okhttpInterceptor = "4.4.1"
            const val json = "20180130"
            const val sshj = "0.27.0"
            const val ktorOkHttp = "1.3.2"
            const val filemanager = "1.0.0"
            const val wireguard = "1.0.20200407"
            const val mahEncryptor = "1.0.1"

            object MerseyLibs {
                const val version = "1.2.3"
            }
        }

        object MultiPlatform {
            const val kotlinStdLib = Versions.kotlin

            const val coroutines = "1.3.5"
            const val serialization = "0.20.0"
            const val ktorClient = "1.3.2"
            const val ktorClientLogging = ktorClient

            const val mokoMvvm = "0.6.0"
            const val mokoResources = Versions.mokoResources

            const val kodein = "6.5.5"
            const val sqlDelight = "1.3.0"
            const val klock = "1.10.0"
            const val preferences = "1.0.0"
            const val settings = "0.6"
        }
    }
}