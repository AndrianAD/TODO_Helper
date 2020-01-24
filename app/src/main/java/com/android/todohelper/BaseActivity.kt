package com.android.todohelper

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    var sharedPreferences = App.instance.getMySharedPreferences()
}