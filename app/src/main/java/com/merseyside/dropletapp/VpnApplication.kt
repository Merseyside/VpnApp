package com.merseyside.dropletapp

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import com.github.shadowsocks.Core
import com.merseyside.archy.BaseApplication
import com.merseyside.dropletapp.connectionTypes.Builder
import com.merseyside.dropletapp.data.db.VpnDatabase
import com.merseyside.dropletapp.di.*
import com.merseyside.dropletapp.presentation.di.component.AppComponent
import com.merseyside.dropletapp.presentation.di.component.DaggerAppComponent
import com.merseyside.dropletapp.presentation.di.module.AppModule
import com.merseyside.dropletapp.presentation.di.module.SubscriptionModule
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.dropletapp.utils.AccountManagerAndroid
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import javax.inject.Inject

class VpnApplication : BaseApplication() {

    lateinit var appComponent : AppComponent
        private set

    @Inject
    lateinit var databaseName: String

    @Inject
    lateinit var subscriptionManager: SubscriptionManager

    override fun onCreate() {
        super.onCreate()

        instance = this

        appComponent = buildComponent()
        appComponent.inject(this)

        initCalligraphy()
        initDB()
        initAccountManager()
        initConnectionTypeBuilder()
        initSubscriptionManager()
        appContext = this

        Core.init(this, context.javaClass.kotlin)
    }

    private fun buildComponent() =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .subscriptionModule(SubscriptionModule(this))
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

    private fun initSubscriptionManager() {
        subsManager = subscriptionManager
    }

    private fun initConnectionTypeBuilder() {
        connectionTypeBuilder = Builder().setContext(this)
    }

    private fun initAccountManager() {
        accountManager = AccountManagerAndroid(this)
    }

    private fun initCalligraphy() {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/ubuntu_condenced.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                ).build()
        )
    }

    companion object {

        private lateinit var instance: VpnApplication

        fun getInstance() : VpnApplication {
            return instance
        }
    }
}