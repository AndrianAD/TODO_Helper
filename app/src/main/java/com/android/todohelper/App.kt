package com.android.todohelper

import android.app.Application
import android.content.SharedPreferences
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

}

