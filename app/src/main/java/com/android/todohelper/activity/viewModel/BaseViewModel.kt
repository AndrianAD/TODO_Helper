package com.android.todohelper.activity.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.todohelper.App
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.retrofit.Repository
import com.android.todohelper.utils.SingleLiveEvent
import com.android.todohelper.utils.hasNetworkConnection
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get


class BaseViewModel : AndroidViewModel(App.instance), KoinComponent {
    var repository: Repository = get()
    val toastMessage = SingleLiveEvent<String>()
    val editEventLiveData = SingleLiveEvent<NetworkResponse<Any>>()
    val getEventsLiveData = SingleLiveEvent<NetworkResponse<Any>>()
    val loginLiveData = SingleLiveEvent<NetworkResponse<Any>>()
    val createEventLiveData = SingleLiveEvent<NetworkResponse<Any>>()
    val addEventToUserLiveData = SingleLiveEvent<NetworkResponse<Any>>()
    val notifyUserLiveData = SingleLiveEvent<NetworkResponse<Any>>()


    fun login(email: String, password: String) {
        if (hasNetworkConnection()) {
            repository.login(email = email, password = password, callback = loginLiveData)
        }
        else {
            loginLiveData.postValue(NetworkResponse.Error("No Internet"))
        }
    }

    fun addEventToUser(email: String, eventId: Int) {
        if (hasNetworkConnection()) {
            repository.addEventToUser(email, eventId, addEventToUserLiveData)
        }
        else {
            loginLiveData.postValue(NetworkResponse.Error("No Internet"))
        }
    }

    // Retrofit coroutine
    fun getEvents(id: Int) {
        viewModelScope.launch {
            if (hasNetworkConnection()) {
                getEventsLiveData.postValue(NetworkResponse.Success(repository.getEvents(id)))
            }
            else {
                getEventsLiveData.postValue(NetworkResponse.Error("No Internet"))
            }
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
        }
        else editEventLiveData.postValue(NetworkResponse.Error("No Internet"))
    }


    fun showToast(message: String) {
        toastMessage.value = message
    }

    fun createEvent(
        name: String,
        description: String = "",
        time: String = "",
        sortOrder: Int,
        id: Int) {
        if (hasNetworkConnection()) {
            repository.createEvent(
                    name = name,
                    description = description,
                    time = time,
                    sortOrder = sortOrder, id = id,
                    callback = createEventLiveData
                                  )
        }
        else editEventLiveData.postValue(NetworkResponse.Error("No Internet"))
    }


    fun notifyUser(email: String, message: String) {
        if (hasNetworkConnection()) {
            repository.notify(
                    email, message, notifyUserLiveData)
        }
        else editEventLiveData.postValue(NetworkResponse.Error("No Internet"))
    }
}




