object FilemanagerDeps {
    object Plugins {
        const val kotlinSerialization =
            "org.jetbrains.kotlin:kotlin-serialization:${FilemanagerVersions.Plugins.serialization}"
        const val androidExtensions =
            "org.jetbrains.kotlin:kotlin-android-extensions:${FilemanagerVersions.Plugins.androidExtensions}"
    }

    object Libs {

        val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${FilemanagerVersions.Libs.kotlinStdLib}"
        val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${FilemanagerVersions.Libs.coroutines}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${FilemanagerVersions.Libs.coroutines}"
        val appCompat = "androidx.appcompat:appcompat:${FilemanagerVersions.Libs.appCompat}"
        val material = "com.google.android.material:material:${FilemanagerVersions.Libs.material}"
        val fragment = "androidx.fragment:fragment-ktx:${FilemanagerVersions.Libs.fragment}"
        val recyclerView = "androidx.recyclerview:recyclerview:${FilemanagerVersions.Libs.recyclerView}"
        val constraintLayout = "androidx.constraintlayout:constraintlayout:${FilemanagerVersions.Libs.constraintLayout}"
        val lifecycle = "androidx.lifecycle:lifecycle-extensions:${FilemanagerVersions.Libs.lifecycle}"
        val cardView = "androidx.cardview:cardview:${FilemanagerVersions.Libs.cardView}"
        val annotation = "androidx.annotation:annotation:${FilemanagerVersions.Libs.appCompat}"

        val zip4j = "net.lingala.zip4j:zip4j:${FilemanagerVersions.Libs.zip4j}"

    }

    val plugins: Map<String, String> = mapOf(
        "kotlin-android-extensions" to Plugins.androidExtensions,
        "kotlinx-serialization" to Plugins.kotlinSerialization
    )
}