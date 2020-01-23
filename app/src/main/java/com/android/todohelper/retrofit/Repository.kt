package com.android.todohelper.retrofit

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.android.tegritee.retrofit.RetrofitFactory
import com.android.todohelper.App
import com.android.todohelper.data.Event
import com.android.todohelper.data.User
import com.android.todohelper.utils.SHARED_PREF
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository() {
    private var retrofit = RetrofitFactory.retrofitInstance
    val sharedPreferences: SharedPreferences =
        App.instance.getSharedPreferences(
            SHARED_PREF,
            Context.MODE_PRIVATE
        )


    fun deleteEvent(eventId: Int) {
        retrofit!!.delete(eventId).enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}
            override fun onFailure(call: Call<String?>, t: Throwable) {}
        })
    }


    fun login(email: String, password: String): MutableLiveData<NetworkResponse<Any>> {
        val callback = MutableLiveData<NetworkResponse<Any>>()
        retrofit!!.login(email, password).enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                callback.value = t.message?.let { NetworkResponse.Error(it) }
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                callback.value = response.body()?.let { NetworkResponse.Success(it) }
            }
        })
        return callback
    }


    fun getEvents(id: Int): MutableLiveData<NetworkResponse<Any>> {
        val callback = MutableLiveData<NetworkResponse<Any>>()
        retrofit!!.read(id).enqueue(object : Callback<ArrayList<Event>> {
            override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
                callback.value = t.message?.let { NetworkResponse.Error(it) }
            }

            override fun onResponse(
                call: Call<ArrayList<Event>>,
                response: Response<ArrayList<Event>>
            ) {
                callback.value = response.body()?.let { NetworkResponse.Success(it) }
            }

        })
        return callback
    }

    fun changeOrder(id: Int, targetOrder: Int) {
        retrofit!!.changeOrder(id, targetOrder).enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}
            override fun onFailure(call: Call<String?>, t: Throwable) {}
        })
    }
}


sealed class NetworkResponse<out T : Any> {
    data class Success<out T : Any>(val output: T) : NetworkResponse<T>()
    data class Error(val message: String) : NetworkResponse<Nothing>()

}

