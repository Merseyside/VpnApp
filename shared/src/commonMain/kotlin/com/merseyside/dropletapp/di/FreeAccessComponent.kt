package com.merseyside.dropletapp.di

import com.merseyside.dropletapp.data.repository.FreeAccessRepositoryImpl
import com.merseyside.dropletapp.domain.repository.FreeAccessRepository
import com.merseyside.dropletapp.filemanager.FileManager
import com.merseyside.dropletapp.freeAccess.FreeAccessApi
import com.merseyside.dropletapp.utils.SettingsHelper
import com.russhwolf.settings.Settings
import com.russhwolf.settings.invoke
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

internal val freeAccessModule = Kodein.Module("free_access") {
    bind<FileManager>() with singleton { FileManager() }

    bind<FreeAccessApi>() with singleton { FreeAccessApi( instance()) }

    bind<Settings>() with singleton { Settings() }

    bind<SettingsHelper>() with singleton { SettingsHelper(instance()) }

    bind<FreeAccessRepository>() with singleton { FreeAccessRepositoryImpl(instance(), instance(), instance()) }
}

internal val freeAccessComponent = Kodein {

    extend(appComponent)

    import(freeAccessModule)
}