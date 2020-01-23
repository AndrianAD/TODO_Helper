package com.android.todohelper.data
import com.google.gson.annotations.SerializedName

data class Event(
    var name: String,
    var description: String,
    var time: String,
    @SerializedName("sort_order")
    var sortOrder: Int,
    @SerializedName("event_id")
    var event_id: String
)