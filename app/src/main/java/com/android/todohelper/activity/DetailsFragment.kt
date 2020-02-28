package com.android.todohelper.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.todohelper.R
import com.android.todohelper.data.Event
import com.android.todohelper.utils.FragmentCallbackToActivity
import com.android.todohelper.utils.OnSwipeTouchListener
import kotlinx.android.synthetic.main.details_dialog.view.*


class DetailsFragment(var event: Event) : DialogFragment() {
    lateinit var fragmentCallbackToActivity: FragmentCallbackToActivity
    companion object {
        const val TAG: String = "DialogRegisterUser"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentCallbackToActivity = context as FragmentCallbackToActivity
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
            .inflate(R.layout.details_dialog, null)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        view.name.text = event.name
        view.description.text = event.description

        view.name.setOnClickListener {

        }

        view.description.setOnClickListener {

        }

        view.mainLayout.setOnTouchListener(object : OnSwipeTouchListener(activity) {
            override fun onSwipeTop() {
                dialog!!.dismiss()
            }

            override fun onSwipeRight() {
                Toast.makeText(activity, "right", Toast.LENGTH_SHORT).show()
            }

            override fun onSwipeLeft() {
                dialog!!.dismiss()
            }

            override fun onSwipeBottom() {
                Toast.makeText(activity, "bottom", Toast.LENGTH_SHORT).show()
            }

        })
    }
}

