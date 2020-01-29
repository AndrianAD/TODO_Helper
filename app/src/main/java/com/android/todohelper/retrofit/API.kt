package com.android.todohelper.retrofit

import com.android.todohelper.data.Event
import com.android.todohelper.data.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface API {


    // suspend
    @POST("Read.php")
    @FormUrlEncoded
    suspend fun read(@Field("id") user_id: Int): ArrayList<Event>

    @POST("Register.php")
    @FormUrlEncoded
    fun Register(
        @Field("name") name: String,
        @Field("lastname") lastname: String,
        @Field("password") password: String,
        @Field("email") email: String): Call<String>

    @POST("Login.php")
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String): Call<List<User>>


    @POST("Create_Event.php")
    @FormUrlEncoded
    fun createEvent(
        @Field("name") name: String?,
        @Field("description") description: String?,
        @Field("time") time: String?,
        @Field("sortOrder") sortOrder: Int,
        @Field("id") user_id: Int): Call<String>


    @POST("Edit_Event.php")
    @FormUrlEncoded
    fun editEvent(
        @Field("name") name: String?,
        @Field("description") description: String?,
        @Field("id") user_id: Int): Call<String>


    @POST("Delete_Event(json).php")
    @FormUrlEncoded
    fun delete(@Field("event_id") event_id: Int): Call<String>


    @POST("ChangeOrder.php")
    @FormUrlEncoded
    fun changeOrder(
        @Field("event_id") event_id: Int,
        @Field("order") order: Int): Call<String>


    @POST("add_event_to_user.php")
    @FormUrlEncoded
    fun addEventToUser(
        @Field("email") email: String,
        @Field("event_id") eventId: Int): Call<String>


    @POST("push_notification.php")
    @FormUrlEncoded
    fun notify(
        @Field("email") email: String,
        @Field("message") message: String): Call<String>


}





