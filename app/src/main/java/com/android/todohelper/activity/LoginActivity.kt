package com.android.todohelper.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.lifecycle.Observer
import com.android.todohelper.App
import com.android.todohelper.R
import com.android.todohelper.activity.viewModel.BaseViewModel
import com.android.todohelper.data.User
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.utils.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.IOException

const val PERMISSION_REQUEST_RECORD_AUDIO = 0

class LoginActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sharedPreferences!!.contains(SHARED_CURRENT_USER)) {
            var json = sharedPreferences!!.get(SHARED_CURRENT_USER, "")
            startActivity(userActivityIntent(Gson().fromJson(json, User::class.java)))
            overridePendingTransition(0, 0)
            finish()
        }
        setContentView(R.layout.activity_login)
        setupUI(window.decorView.rootView)
        checkPermission()
        var viewModel: BaseViewModel = getViewModel()



        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.loginLiveData.observe(this, Observer {
            when (it) {
                is NetworkResponse.Success -> {
                    it.output as List<User>
                    if (it.output.isEmpty()) {
                        makeAllertDialogNO(message = "Login Failed", negativeButton = "Retry")
                        return@Observer
                    }
                    else {
                        sharedPreferences!!.put(SHARED_CURRENT_USER, Gson().toJson(it.output[0]))
                        sendFirebaseToken(it.output[0].id)
                        startActivity(userActivityIntent(it.output[0]))
                    }

                }
                is NetworkResponse.Error -> toast(it.message)
            }
        })


        bSignIn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isEmpty().not() && password.isEmpty().not()) {
                viewModel.login(email = email, password = password)

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
                                           ) {
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {

            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mainLayout.showSnackbar("permission_granted", Snackbar.LENGTH_SHORT)
                // ------go to next
            }
            else {
                // Permission request was denied.
                mainLayout.showSnackbar("permission_denied", Snackbar.LENGTH_SHORT)
            }
        }
    }

    private fun checkPermission() {

        if (checkSelfPermissionCompat(Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            mainLayout.showSnackbar("Permission is available", Snackbar.LENGTH_SHORT)
            // ------go to next
        }
        else {

            requestPermission()
        }
    }

    private fun requestPermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.RECORD_AUDIO)) {
            mainLayout.showSnackbar(
                    "access_required",
                    Snackbar.LENGTH_INDEFINITE, "Ok"
                                   ) {
                requestPermissionsCompat(
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        PERMISSION_REQUEST_RECORD_AUDIO
                                        )
            }
        }
        else {
            mainLayout.showSnackbar("permission_not_available", Snackbar.LENGTH_SHORT)
            requestPermissionsCompat(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSION_REQUEST_RECORD_AUDIO
                                    )
        }
    }


    private fun sendFirebaseToken(userId:Int) {
        var token = App.token
        if (App.token.length < 2) {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    token = task.result?.token!!

                })
            toast("${App.token} -> $token")
            App.token = token
        }
        val client = OkHttpClient()
        val body: RequestBody = FormBody.Builder()
            .add("Token", token)
            .add("user_id", userId.toString())
            .build()

        val request = Request.Builder()
            .url("http://uncroptv.000webhostapp.com/register.php")
            .post(body)
            .build()

        try {
            CoroutineScope(Dispatchers.IO).launch {
                client.newCall(request).execute()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}


