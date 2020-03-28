package com.merseyside.dropletapp

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.di.accountManager
import com.merseyside.dropletapp.di.appContext
import com.merseyside.dropletapp.di.sqlDriver
import com.merseyside.dropletapp.presentation.di.component.AppComponent
import com.merseyside.dropletapp.presentation.di.component.DaggerAppComponent
import com.merseyside.dropletapp.presentation.di.module.AppModule
import com.merseyside.dropletapp.utils.AccountManagerAndroid
import com.merseyside.dropletapp.utils.RemoteConfigHelper
import com.merseyside.merseyLib.BaseApplication
import com.squareup.sqldelight.android.AndroidSqliteDriver
import javax.inject.Inject

class VpnApplication : BaseApplication() {

    lateinit var appComponent : AppComponent
        private set

    @Inject
    lateinit var databaseName: String

    override fun onCreate() {
        super.onCreate()

        instance = this

        appComponent = buildComponent()
        appComponent.inject(this)

        initDB()
        initAccountManager()
        appContext = this
    }

    private fun buildComponent() =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

    private fun initDB() {
        val config = SupportSQLiteOpenHelper.Configuration.builder(this)
            .name(databaseName)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    val driver = AndroidSqliteDriver(db)
                    VpnDatabase.Schema.create(driver)
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}

            })
            .build()

        val sqlHelper = FrameworkSQLiteOpenHelperFactory().create(config)

        sqlDriver = AndroidSqliteDriver(sqlHelper)
    }

    private fun initAccountManager() {
        accountManager = AccountManagerAndroid(this)
    }

    companion object {
        private const val TAG = "VpnApplication"

        private lateinit var instance: VpnApplication

        fun getInstance() : VpnApplication {
            return instance
        }
    }
}