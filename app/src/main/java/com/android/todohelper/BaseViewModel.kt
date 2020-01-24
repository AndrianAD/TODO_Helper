package com.android.todohelper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.retrofit.Repository
import com.android.todohelper.utils.SingleLiveEvent

import org.koin.core.KoinComponent
import org.koin.core.get


class BaseViewModel : AndroidViewModel(App.instance), KoinComponent {
    var repository: Repository = get()
    val toastMessage = SingleLiveEvent<String>()
    val editEventLiveData = SingleLiveEvent<NetworkResponse<Any>>()
    val getEventsLiveData = SingleLiveEvent<NetworkResponse<Any>>()
    val loginLiveData = SingleLiveEvent<NetworkResponse<Any>>()
    val createEventLiveData = SingleLiveEvent<NetworkResponse<Any>>()


    fun login(email: String, password: String) {
        if (hasNetworkConnection()) {
            repository.login(email = email, password = password, callback = loginLiveData)
        } else {
            loginLiveData.postValue(NetworkResponse.Error("No Internet"))
        }
    }

    fun getEvents(id: Int) {
        if (hasNetworkConnection()) {
            repository.getEvents(id, getEventsLiveData)
        } else {
            getEventsLiveData.postValue(NetworkResponse.Error("No Internet"))
        }
    }

    fun editEvent(
        name: String = "",
        description: String = "",
        id: Int = -1
    ) {
        if (hasNetworkConnection()) {
            repository.editEvent(
                toName = name,
                toDescription = description,
                toId = id, callback = editEventLiveData
            )
        } else editEventLiveData.postValue(NetworkResponse.Error("No Internet"))
    }


    fun showToast(message: String) {
        toastMessage.value = message
    }

    fun createEvent(name: String, description: String="", time: String="", sortOrder: Int, id: Int) {
        if (hasNetworkConnection()) {
            repository.createEvent(
                name = name,
                description = description,
                time = time,
                sortOrder = sortOrder, id = id,
                callback = createEventLiveData
            )
        } else editEventLiveData.postValue(NetworkResponse.Error("No Internet"))
    }

    private fun hasNetworkConnection(): Boolean {
        val cm =
            App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo
                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val n = cm.activeNetwork
                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)
                    return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                }
            }
        }
        return false
    }
}



