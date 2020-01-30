package com.android.todohelper.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.todohelper.R
import com.android.todohelper.activity.viewModel.RegisterActivityViewModel
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.utils.isEmpty
import com.android.todohelper.utils.toast
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class RegisterActivity : AppCompatActivity() {
    lateinit var viewModel: RegisterActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = getViewModel()


        viewModel.registerUserLiveData.observe(this, Observer {
            when (it) {
                is NetworkResponse.Success -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                is NetworkResponse.Error -> {
                    toast(it.message)

                }
            }
        })
        btnRegister.setOnClickListener {
            if (isValid(name, lastName, email, password)) {
                viewModel.registerUser(
                        name = name.editText?.text.toString(),
                        lastName = lastName.editText?.text.toString(),
                        email = email.editText?.text.toString(),
                        password = password.editText?.text.toString())
            }
        }


    }

    fun isValid(vararg textInput: TextInputLayout): Boolean {
        var isValid = true
        for (i in textInput) {
            i.apply {
                if (editText!!.isEmpty()) {
                    error = "Заполните пожалуйста поле: "
                    editText!!.error = "Заполните пожалуйста поле: "
                    isValid = false
                }
            }
        }

        return isValid
    }
}
