package com.android.todohelper

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.android.todohelper.utils.SHARED_PREF
import com.android.todohelper.utils.firstModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    companion object {
        lateinit var instance: App
         private var sharedPreferences: SharedPreferences? = null

    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(firstModule)
        }


    }

    fun getMySharedPreferences(): SharedPreferences? {
        return if (sharedPreferences == null) {
            getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        } else
            sharedPreferences
    }

}

