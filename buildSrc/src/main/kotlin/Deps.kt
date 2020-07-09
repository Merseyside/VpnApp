object Deps {
    object Plugins {
        val androidApplication = PluginDesc(id = "com.android.application")
        val kotlinKapt = PluginDesc(id = "kotlin-kapt")
        val kotlinAndroid = PluginDesc(id = "kotlin-android")
        val kotlinAndroidExtensions = PluginDesc(id = "kotlin-android-extensions")
        val mobileMultiplatform = PluginDesc(id = "dev.icerock.mobile.multiplatform")

        val androidLibrary = PluginDesc(
            id = "com.android.library",
            module = "com.android.tools.build:gradle:${Versions.Plugins.android}"
        )

        val kotlinMultiplatform = PluginDesc(
            id = "org.jetbrains.kotlin.multiplatform",
            module = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Plugins.kotlin}"
        )

        val kotlinSerialization = PluginDesc(
            id = "kotlinx-serialization",
            module = "org.jetbrains.kotlin:kotlin-serialization:${Versions.Plugins.serialization}"
        )

        val androidMaven = PluginDesc(
            id = "com.github.dcendents.android-maven",
            module = "com.github.dcendents:android-maven-gradle-plugin:${Versions.Plugins.maven}"
        )

        val sqlDelight = PluginDesc(
            id = "com.squareup.sqldelight",
            module = "com.squareup.sqldelight:gradle-plugin:${Versions.Plugins.sqlDelight}"
        )

        val buildKonfig = PluginDesc(
            id = "com.codingfeline.buildkonfig",
            module = "com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:${Versions.Plugins.buildKonfig}"
        )
    }

    object Libs {
        object Android {
            val kotlinStdLib = AndroidLibrary(
                name = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Libs.Android.kotlinStdLib}"
            )
            val coroutinesCore = AndroidLibrary(
                name = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Libs.Android.coroutines}"
            )
            val coroutines = AndroidLibrary(
                name = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Libs.Android.coroutines}"
            )
            val appCompat = AndroidLibrary(
                name = "androidx.appcompat:appcompat:${Versions.Libs.Android.appCompat}"
            )
            val material = AndroidLibrary(
                name = "com.google.android.material:material:${Versions.Libs.Android.material}"
            )
            val fragment = AndroidLibrary(
                name = "androidx.fragment:fragment-ktx:${Versions.Libs.Android.fragment}"
            )
            val recyclerView = AndroidLibrary(
                name = "androidx.recyclerview:recyclerview:${Versions.Libs.Android.recyclerView}"
            )
            val constraintLayout = AndroidLibrary(
                name = "androidx.constraintlayout:constraintlayout:${Versions.Libs.Android.constraintLayout}"
            )
            val lifecycle = AndroidLibrary(
                name = "androidx.lifecycle:lifecycle-extensions:${Versions.Libs.Android.lifecycle}"
            )
            val cardView = AndroidLibrary(
                name = "androidx.cardview:cardview:${Versions.Libs.Android.cardView}"
            )
            val annotation = AndroidLibrary(
                name = "androidx.annotation:annotation:${Versions.Libs.Android.appCompat}"
            )
            val paging = AndroidLibrary(
                name = "android.arch.paging:runtime:${Versions.Libs.Android.paging}"
            )
            val preferences = AndroidLibrary(
                name = "androidx.preference:preference:${Versions.Libs.Android.preferences}"
            )
            val reflect = AndroidLibrary(
                name = "org.jetbrains.kotlin:kotlin-reflect:${Versions.Libs.Android.kotlinStdLib}"
            )
            val billing = AndroidLibrary(
                name = "com.android.billingclient:billing:${Versions.Libs.Android.billing}"
            )
            val billingKtx = AndroidLibrary(
                name = "com.android.billingclient:billing-ktx:${Versions.Libs.Android.billing}"
            )
            val firebaseFirestore = AndroidLibrary(
                name = "com.google.firebase:firebase-firestore-ktx:${Versions.Libs.Android.firebaseFirestore}"
            )
            val firebaseCore = AndroidLibrary(
                name = "com.google.firebase:firebase-core:${Versions.Libs.Android.firebaseCore}"
            )
            val firebaseConfig = AndroidLibrary(
                name = "com.google.firebase:firebase-config:${Versions.Libs.Android.firebaseConfig}"
            )
            val firebaseAnalytics = AndroidLibrary(
                name = "com.google.firebase:firebase-analytics:${Versions.Libs.Android.firebaseAnalytics}"
            )
            val oauth2 = AndroidLibrary(
                name = "com.google.auth:google-auth-library-oauth2-http:${Versions.Libs.Android.auth}"
            )
            val room = AndroidLibrary(
                name = "android.arch.persistence.room:rxjava2:${Versions.Libs.Android.room}"
            )
            val roomCompiler = KaptLibrary(
                name = "android.arch.persistence.room:compiler:${Versions.Libs.Android.room}"
            )

            val dagger = AndroidLibrary(
                name = "com.google.dagger:dagger:${Versions.Libs.Android.dagger}"
            )
            val daggerCompiler = KaptLibrary(
                name = "com.google.dagger:dagger-compiler:${Versions.Libs.Android.dagger}"
            )

            val navigation = AndroidLibrary(
                name = "androidx.navigation:navigation-fragment-ktx:${Versions.Libs.Android.navigation}"
            )
            val navigationUi = AndroidLibrary(
                name = "androidx.navigation:navigation-ui-ktx:${Versions.Libs.Android.navigation}"
            )
            val keyboard = AndroidLibrary(
                name = "net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:${Versions.Libs.Android.keyboard}"
            )
            val worker = AndroidLibrary(
                name = "androidx.work:work-runtime-ktx:${Versions.Libs.Android.worker}"
            )
            val gson = AndroidLibrary(
                name = "com.google.code.gson:gson:${Versions.Libs.Android.gson}"
            )
            val cicerone = AndroidLibrary(
                name = "ru.terrakok.cicerone:cicerone:${Versions.Libs.Android.cicerone}"
            )
            val selectorView = AndroidLibrary(
                name = "com.github.Merseyside.horizontal-selector-view:HorizontalSelectorView:${Versions.Libs.Android.selectorView}"
            )
            val qrGen = AndroidLibrary(
                name = "com.github.kenglxn:QRGen:${Versions.Libs.Android.qrGen}"
            )
            val okhttp = AndroidLibrary(
                name = "com.squareup.okhttp3:okhttp:${Versions.Libs.Android.okhttp}"
            )
            val okhttpInterceptor = AndroidLibrary(
                name = "com.squareup.okhttp3:logging-interceptor:${Versions.Libs.Android.okhttpInterceptor}"
            )
            val json = AndroidLibrary(
                name = "org.json:json:${Versions.Libs.Android.json}"
            )
            val sshj = AndroidLibrary(
                name = "com.hierynomus:sshj:${Versions.Libs.Android.sshj}"
            )
            val ktorOkHttp = AndroidLibrary(
                name = "io.ktor:ktor-client-okhttp:${Versions.Libs.Android.ktorOkHttp}"
            )
            val filemanager = AndroidLibrary(
                name = "com.github.Merseyside:android-filemanager:${Versions.Libs.Android.filemanager}"
            )

            val wireguard = AndroidLibrary(
                name = "com.wireguard.android:tunnel:${Versions.Libs.Android.wireguard}"
            )
            val mahEncryptor = AndroidLibrary(
                name = "com.mobapphome.library:mah-encryptor-lib:${Versions.Libs.Android.mahEncryptor}"
            )

            object MerseyLibs {
                private val base = "com.github.Merseyside.mersey-android-library"

                val cleanMvvmArch = AndroidLibrary(
                    name = "$base:clean-mvvm-arch:${Versions.Libs.Android.MerseyLibs.version}:standart@aar"
                )

                val adapters = AndroidLibrary(
                    name = "$base:adapters:${Versions.Libs.Android.MerseyLibs.version}"
                )

                val animators = AndroidLibrary(
                    name = "$base:animators:${Versions.Libs.Android.MerseyLibs.version}"
                )

                val utils = AndroidLibrary(
                    name = "$base:utils:${Versions.Libs.Android.MerseyLibs.version}"
                )
            }
        }

        object MultiPlatform {
            val kotlinStdLib = MultiPlatformLibrary(
                android = Android.kotlinStdLib.name,
                common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.Libs.MultiPlatform.kotlinStdLib}"
            )
            val coroutines = MultiPlatformLibrary(
                android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Libs.MultiPlatform.coroutines}",
                common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.Libs.MultiPlatform.coroutines}",
                ios = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.Libs.MultiPlatform.coroutines}"
            )
            val serialization = MultiPlatformLibrary(
                android = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.Libs.MultiPlatform.serialization}",
                common = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.Libs.MultiPlatform.serialization}",
                ios = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.Libs.MultiPlatform.serialization}"
            )
            val ktorClient = MultiPlatformLibrary(
                android = "io.ktor:ktor-client-android:${Versions.Libs.MultiPlatform.ktorClient}",
                common = "io.ktor:ktor-client-core:${Versions.Libs.MultiPlatform.ktorClient}",
                ios = "io.ktor:ktor-client-ios:${Versions.Libs.MultiPlatform.ktorClient}"
            )
            val ktorClientLogging = MultiPlatformLibrary(
                android = "io.ktor:ktor-client-logging-jvm:${Versions.Libs.MultiPlatform.ktorClientLogging}",
                common = "io.ktor:ktor-client-logging:${Versions.Libs.MultiPlatform.ktorClientLogging}",
                ios = "io.ktor:ktor-client-logging-native:${Versions.Libs.MultiPlatform.ktorClientLogging}"
            )

            val mokoMvvm = MultiPlatformLibrary(
                common = "dev.icerock.moko:mvvm:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosX64 = "dev.icerock.moko:mvvm-iosx64:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosArm64 = "dev.icerock.moko:mvvm-iosarm64:${Versions.Libs.MultiPlatform.mokoMvvm}"
            )
            val mokoResources = MultiPlatformLibrary(
                common = "dev.icerock.moko:resources:${Versions.Libs.MultiPlatform.mokoResources}",
                iosX64 = "dev.icerock.moko:resources-iosx64:${Versions.Libs.MultiPlatform.mokoResources}",
                iosArm64 = "dev.icerock.moko:resources-iosarm64:${Versions.Libs.MultiPlatform.mokoResources}"
            )
            val kodein = MultiPlatformLibrary(
                common = "org.kodein.di:kodein-di-core:${Versions.Libs.MultiPlatform.kodein}"
            )
            val kodeinErased = MultiPlatformLibrary(
                common = "org.kodein.di:kodein-di-erased:${Versions.Libs.MultiPlatform.kodein}"
            )

            val sqlDelight = MultiPlatformLibrary(
                common = "com.squareup.sqldelight:runtime:${Versions.Libs.MultiPlatform.sqlDelight}",
                android = "com.squareup.sqldelight:android-driver:${Versions.Libs.MultiPlatform.sqlDelight}"
            )
            val klock = MultiPlatformLibrary(
                common = "com.soywiz.korlibs.klock:klock:${Versions.Libs.MultiPlatform.klock}"
            )
            val preferences = MultiPlatformLibrary(
                common = "com.github.florent37:multiplatform-preferences:${Versions.Libs.MultiPlatform.preferences}",
                android = "com.github.florent37:multiplatform-preferences-android:${Versions.Libs.MultiPlatform.preferences}"
            )
            val settings = MultiPlatformLibrary(
                common = "com.russhwolf:multiplatform-settings-no-arg:${Versions.Libs.MultiPlatform.settings}"
                //android = "com.github.florent37:multiplatform-preferences-android:${Versions.Libs.MultiPlatform.preferences}"
            )
        }
    }
}