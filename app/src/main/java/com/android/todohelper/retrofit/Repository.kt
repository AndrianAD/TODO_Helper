package com.android.todohelper.retrofit

import android.content.Context
import android.content.SharedPreferences
import com.android.todohelper.App
import com.android.todohelper.data.User
import com.android.todohelper.utils.SHARED_PREF
import com.android.todohelper.utils.SingleLiveEvent
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


    fun login(email: String, password: String, callback: SingleLiveEvent<NetworkResponse<Any>>) {
        retrofit!!.login(email, password).enqueue(object : Callback<List<User>> {
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                callback.value = t.message?.let { NetworkResponse.Error(it) }
            }

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                callback.value = response.body()?.let { NetworkResponse.Success(it) }
            }
        })
    }


    suspend fun getEvents(id: Int) = retrofit!!.read(id)


//    fun getEvents(
//        id: Int,
//        callback: SingleLiveEvent<NetworkResponse<Any>>
//                 ) {
//        retrofit!!.read(id).enqueue(object : Callback<ArrayList<Event>> {
//            override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
//                callback.value = t.message?.let { NetworkResponse.Error(it) }
//            }
//
//            override fun onResponse(
//                call: Call<ArrayList<Event>>,
//                response: Response<ArrayList<Event>>
//                                   ) {
//                callback.value = response.body()?.let { NetworkResponse.Success(it) }
//            }


    fun changeOrder(id: Int, targetOrder: Int) {
        retrofit!!.changeOrder(id, targetOrder).enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}
            override fun onFailure(call: Call<String?>, t: Throwable) {}
        })
    }

    fun editEvent(
        toName: String,
        toDescription: String,
        toId: Int, callback: SingleLiveEvent<NetworkResponse<Any>>
                 ) {
        retrofit!!.editEvent(toName, toDescription, toId).enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                callback.postValue(response.body()?.let { NetworkResponse.Success(it) })
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                callback.postValue(t.message?.let { NetworkResponse.Error(it) })
            }
        })
    }

    fun createEvent(
        name: String,
        description: String,
        time: String,
        sortOrder: Int,
        id: Int,
        callback: SingleLiveEvent<NetworkResponse<Any>>
                   ) {
        retrofit!!.createEvent(name, description, time, sortOrder, id)
            .enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    callback.postValue(response.body()?.let { NetworkResponse.Success(it) })
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    callback.postValue(t.message?.let { NetworkResponse.Error(it) })
                }
            })
    }

    fun addEventToUser(
        email: String,
        eventId: Int,
        callback: SingleLiveEvent<NetworkResponse<Any>>) {
        retrofit!!.addEventToUser(email, eventId)
            .enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    callback.postValue(response.body()?.let { NetworkResponse.Success(it) })
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    callback.postValue(t.message?.let { NetworkResponse.Error(it) })
                }
            })
    }

    fun notify(email: String, message: String, callback: SingleLiveEvent<NetworkResponse<Any>>) {
        retrofit!!.notify(email, message)
            .enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    callback.postValue(response.body()?.let { NetworkResponse.Success(it) })
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    callback.postValue(t.message?.let { NetworkResponse.Error(it) })
                }
            })
    }

    fun registerUser(
        name: String,
        lastName: String,
        email: String,
        password: String,
        callback: SingleLiveEvent<NetworkResponse<Any>>) {
        retrofit!!.Register(name, lastName, email, password)
            .enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    callback.postValue(response.body()?.let { NetworkResponse.Success(it) })
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    callback.postValue(t.message?.let { NetworkResponse.Error(it) })
                }
            })


    }
}


sealed class NetworkResponse<out T : Any> {
    data class Success<out T : Any>(val output: T) : NetworkResponse<T>()
    data class Error(val message: String) : NetworkResponse<Nothing>()

}

