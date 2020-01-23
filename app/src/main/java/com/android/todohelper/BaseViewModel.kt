package com.android.todohelper

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.retrofit.Repository
import com.android.todohelper.utils.SingleLiveEvent
import org.koin.core.KoinComponent
import org.koin.core.get

class BaseViewModel : AndroidViewModel(App.instance), KoinComponent {
    var repository: Repository = get()
    val toastMessage = SingleLiveEvent<String>()


    fun login(email: String, password: String): MutableLiveData<NetworkResponse<Any>> {
        return if (hasNetworkConnection()) {
            repository.login(email = email, password = password)
        } else {
            MutableLiveData(NetworkResponse.Error("No Internet"))
        }
    }


    fun getEvents(id: Int): MutableLiveData<NetworkResponse<Any>> {
        return if (hasNetworkConnection()) {
            repository.getEvents(id)
        } else {
            MutableLiveData(NetworkResponse.Error("No Internet"))
        }
    }


    fun showToast(message: String) {
        toastMessage.value = message
    }

    private fun hasNetworkConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}



