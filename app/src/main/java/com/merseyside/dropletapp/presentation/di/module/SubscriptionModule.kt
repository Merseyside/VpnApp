package com.merseyside.dropletapp.presentation.di.module

import android.content.Context
import com.merseyside.dropletapp.R
import com.merseyside.dropletapp.subscriptions.SubscriptionManager
import com.merseyside.merseyLib.presentation.di.qualifiers.ApplicationContext
import com.merseyside.merseyLib.utils.billing.BillingManager
import com.merseyside.merseyLib.utils.ext.log
import com.mobapphome.mahencryptorlib.MAHEncryptor
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class SubscriptionModule(@ApplicationContext private val context: Context) {

    @Provides
    @Singleton
    fun provideBillingManager(@Named("license") base64Key: String): BillingManager {
        return BillingManager(context, base64Key)
    }

    @Provides
    @Named("license")
    internal fun provideLicenseKey(): String {
        val mahEncryptor = MAHEncryptor.newInstance("If you are a fucking hacker you know I fucked your dog!")

        val key = "GDw3cf8vn2z5sNa0KTyFOPwhYU0UBONUtuPi15PZ/jAmfLjDQqrvxoteHGO2A" +
                "Gr1XSh5lTIyM28Z9BvO57fhMICILhh1ha17vDi0CkPATJcBm6zfbSw8FCFI1sQq" +
                "BU4T4g+QKxWnaAOyks32QB166L36hz3qq2mNG6zHQipVb1/kwQylwsyho1+lQir7" +
                "EO1CA1Dpx6TsPHIoNhBZo6xLOMspFeI0JCsx0edNZo4pNfK6ekdMGpGJ2JU6VAEM" +
                "lJI/15whs1pVRpJqIMpzlVHJMDsetDk+hWXqmg1KUiZJgOYZxU2HVYzQxJxCHO3x" +
                "QASKTHDJnprXTrEC7S1jk0H/7U4R8ihv803LamXt+Or26UyY3fR8Khj3dm/zYkfZC" +
                "DDGFmuQVgA9a5HgaZRSuCBPntL+EY6Xj0TCCydtwanzgTotYilGbX8hyUBXqTlmW/" +
                "wTfdTVTdcezwfA5+wJF4fVtTEdGd4vsFjq8Wh2JAVecaRK7QSbtqqZxWkpxT4bZhd" +
                "z5xSTrgQiqUw63YIBUnIgGQ"

        return mahEncryptor.decode(key).log()
    }


    @Provides
    @Singleton
    fun provideSubscriptionManager(@Named("license") base64Key: String): SubscriptionManager {
        return SubscriptionManager.Builder()
            .setContext(context)
            .setBase64Key(base64Key)
            .setCredentialsId(R.raw.credentials)
            .build()
    }
}