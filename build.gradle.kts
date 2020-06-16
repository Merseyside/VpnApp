allprojects {

    buildscript {
        repositories {
            mavenLocal()

            jcenter()
            google()

            maven { url = uri("https://dl.bintray.com/kotlin/kotlin") }
            maven { url = uri("https://kotlin.bintray.com/kotlinx") }
            maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }
        }

//        dependencies {
//            classpath("gradle.plugin.org.mozilla.rust-android-gradle:plugin:0.8.3")
//        }

//    dependencies {
//        with(LibraryDeps.Plugins) {
//            listOfNotNull(
//                androidLibrary,
//                kotlinMultiplatform,
//                kotlinKapt,
//                kotlinAndroid
//            )
//        }.let { plugins(it) }
//    }
    }
    repositories {
        google()
        jcenter()

        maven { url = uri("https://kotlin.bintray.com/kotlinx")}
        maven { url = uri("https://dl.bintray.com/kotlin/ktor")}
        maven { url = uri("https://dl.bintray.com/kodein-framework/Kodein-DI")}
        maven { url = uri("https://jitpack.io")}
        maven { url = uri("https://dl.bintray.com/korlibs/korlibs")}
        maven { url = uri("https://dl.bintray.com/florent37/maven")}
        maven { url = uri("https://kotlin.bintray.com/kotlin")}
        maven { url = uri("https://dl.bintray.com/icerockdev/moko")}
        maven { url = uri("https://dl.bintray.com/aakira/maven")}
        maven { url = uri("https://jetbrains.bintray.com/kotlin-native-dependencies") }
        maven { url = uri("https://maven.fabric.io/public") }
        maven { url = uri("https://dl.bintray.com/icerockdev/plugins") }

    }
}

tasks.register("clean", Delete::class).configure {
    group = "build"
    delete(rootProject.buildDir)
}