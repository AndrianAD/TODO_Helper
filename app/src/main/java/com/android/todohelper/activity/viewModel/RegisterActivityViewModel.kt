package com.android.todohelper.activity.viewModel

import androidx.lifecycle.AndroidViewModel
import com.android.todohelper.App
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.retrofit.Repository
import com.android.todohelper.utils.SingleLiveEvent
import com.android.todohelper.utils.hasNetworkConnection
import org.koin.core.KoinComponent
import org.koin.core.get

class RegisterActivityViewModel : AndroidViewModel(App.instance), KoinComponent {
    var repository: Repository = get()
    val registerUserLiveData = SingleLiveEvent<NetworkResponse<Any>>()


    fun registerUser(name: String,lastName:String,email: String, password: String) {
        if (hasNetworkConnection()) {
            repository.registerUser(name, lastName, email,password,registerUserLiveData)
        }
        else {
            registerUserLiveData.postValue(NetworkResponse.Error("No Internet"))
        }
    }


}