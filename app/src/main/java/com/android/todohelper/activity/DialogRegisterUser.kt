package com.android.todohelper.activity

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.android.todohelper.R
import com.android.todohelper.data.Event
import kotlinx.android.synthetic.main.dialog_register_user.view.*


class DialogRegisterUser(var event: Event) : DialogFragment() {
    companion object {
        const val TAG: String = "DialogRegisterUser"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {

        return LayoutInflater.from(context)
            .inflate(R.layout.dialog_register_user, null)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        view.name.text = event.name
        view.description.text = event.description
    }
}

