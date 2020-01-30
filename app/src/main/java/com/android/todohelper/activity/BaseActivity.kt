package com.android.todohelper.activity

import androidx.appcompat.app.AppCompatActivity
import com.android.todohelper.App

open class BaseActivity : AppCompatActivity() {
    var sharedPreferences = App.instance.getMySharedPreferences()
}