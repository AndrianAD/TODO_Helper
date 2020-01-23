package com.android.todohelper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.data.User
import com.android.todohelper.utils.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

const val PERMISSION_REQUEST_RECORD_AUDIO = 0

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        chekPermission()

        var viewModel: BaseViewModel = getViewModel()

        tvRegisterLink.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }

        bSignIn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isEmpty().not() && password.isEmpty().not()) {
                viewModel.login(email=email,password = password).observe(this, Observer {
                    when (it) {
                        is NetworkResponse.Success -> {
                            it.output as List<User>
                            startActivity(UserActivityIntent(it.output[0]))
                        }
                        is NetworkResponse.Error -> toast(it.message)
                    }
                })
            } else {
                makeAllertDialogNO(message = "Login Failed", negativeButton = "Retry")
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
                mainLayout.showSnackbar("camera_permission_granted", Snackbar.LENGTH_SHORT)
                // ------go to next
            } else {
                // Permission request was denied.
                mainLayout.showSnackbar("camera_permission_denied", Snackbar.LENGTH_SHORT)
            }
        }
    }

    private fun chekPermission() {

        if (checkSelfPermissionCompat(Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            mainLayout.showSnackbar("Permission is available", Snackbar.LENGTH_SHORT)
            // ------go to next
        } else {

            requestPermission()
        }
    }

    private fun requestPermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.RECORD_AUDIO)) {
            mainLayout.showSnackbar(
                "camera_access_required",
                Snackbar.LENGTH_INDEFINITE, "Ok"
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSION_REQUEST_RECORD_AUDIO
                )
            }
        } else {
            mainLayout.showSnackbar("camera_permission_not_available", Snackbar.LENGTH_SHORT)
            requestPermissionsCompat(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_RECORD_AUDIO
            )
        }
    }

}
