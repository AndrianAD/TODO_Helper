package com.android.todohelper.activity

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.todohelper.App
import java.security.AccessController

open class BaseActivity : AppCompatActivity() {
    var sharedPreferences = App.instance.getMySharedPreferences()



    fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                if (AccessController.getContext() != null)
                    hideKeyboard(this)
                false
            }
        }
        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun hideKeyboard(context: Context) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if ((context as Activity).currentFocus != null) {
            try {
                inputManager.hideSoftInputFromWindow(
                        (context as AppCompatActivity).currentFocus!!.windowToken,
                        0
                                                    )
            } catch (ex: NullPointerException) {
                ex.printStackTrace()
            }

        }
    }
}