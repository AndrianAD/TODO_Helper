package com.android.todohelper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)


        val intent = intent
        val name = intent.getStringExtra("name")
        val lastname = intent.getStringExtra("lastname")

        tvWelcomeMsg.text ="$name  $lastname"





    }
}
