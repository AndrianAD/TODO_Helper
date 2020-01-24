package com.android.todohelper

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.todohelper.data.Event
import com.android.todohelper.dragAndDrop.SimpleItemTouchHelperCallback
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.utils.isEmpty
import com.android.todohelper.utils.preventMultiClick
import com.android.todohelper.utils.toast
import kotlinx.android.synthetic.main.activity_user.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class UserActivity : AppCompatActivity(), RecyclerAdapter.OnClickEvent {

    private lateinit var adapter: RecyclerAdapter
    var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    lateinit var viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        viewModel = getViewModel()


        val intent = intent
        val name = intent.getStringExtra("name")
        val lastname = intent.getStringExtra("lastname")
        var userId: Int = intent.getStringExtra("id").toInt()

        tvWelcomeMsg.text = "$name  $lastname"

        adapter = RecyclerAdapter(context = this, onClickEvent = this)
        adapter.setArrayList(ArrayList())
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        viewModel.getEvents(userId)



        viewModel.getEventsLiveData.observe(this, Observer {
            when (it) {
                is NetworkResponse.Success -> {
                    it.output as ArrayList<Event>
                    adapter.setArrayList(it.output)
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                }
                is NetworkResponse.Error -> toast(it.message)
            }
        })

        viewModel.editEventLiveData.observe(this, Observer {
            when (it) {
                is NetworkResponse.Success -> {
                    progressBar.visibility = View.VISIBLE
                    viewModel.getEvents(userId)
                }
                is NetworkResponse.Error -> toast(it.message)
            }
        })

    }

    override fun onClick(event: Event, position: Int) {

        var dialog = Dialog(this)
        dialog.setContentView(R.layout.save_form)
        dialog.setTitle("Введите название:")
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var dialogButtonOK =
            dialog.findViewById<Button>(R.id.save_form_bt_OK)
        var dialogEtName = dialog.findViewById<EditText>(R.id.save_form_et_name)
        var dialogDescription = dialog.findViewById<EditText>(R.id.save_form_et_description)
        var dialogProgress = dialog.findViewById<ProgressBar>(R.id.dialogProgress)
        dialog.show()
        dialogEtName.post {
            val inputMethodManager: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInputFromWindow(
                dialogEtName.applicationWindowToken, InputMethodManager.SHOW_IMPLICIT, 0
            )
            dialogEtName.requestFocus()
        }

        dialogButtonOK.setOnClickListener {
            if (preventMultiClick()) {
                return@setOnClickListener
            }
            if (dialogEtName.isEmpty()) {
                toast("Заполните название")
                return@setOnClickListener
            }
            //dialogProgress.visibility = View.VISIBLE
            viewModel.editEvent(
                name = dialogEtName.text.toString(),
                description = dialogDescription.text.toString(),
                id = event.eventId
            )
            dialog.dismiss()
        }

    }


}
