allprojects {
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

    }
}

tasks.register("clean", Delete::class).configure {
    group = "build"
    delete(rootProject.buildDir)
}