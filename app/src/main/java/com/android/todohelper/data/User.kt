package com.android.todohelper.data

import com.google.gson.annotations.SerializedName

data class User(
    var name: String,
    @SerializedName("lastname")
    var lastName: String,
    var email: String,
    var password: String,
    var id: Int,
    var role: String
)