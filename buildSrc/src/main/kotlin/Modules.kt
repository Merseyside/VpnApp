object Modules {

    const val isLocalDependencies = false
    const val library = "mersey-android-library"

    object MultiPlatform {

        val cleanMvvmArch = MultiPlatformModule(
            name = ":kmp-clean-mvvm-arch",
            exported = true
        )

        val utils = MultiPlatformModule(
            name = ":kmp-utils",
            exported = true
        )
    }

    object Android {

        const val cleanMvvmArch = ":clean-mvvm-arch"
        const val adapters = ":adapters"
        const val animators = ":animators"
        const val utils = ":utils"

        const val openvpn = ":openvpn-core"
        const val filemanager = ":filemanager"
        const val shadowsocks = ":core"
    }
}