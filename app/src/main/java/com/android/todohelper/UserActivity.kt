package com.android.todohelper

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.todohelper.data.Event
import com.android.todohelper.dragAndDrop.SimpleItemTouchHelperCallback
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.utils.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.IOException

class UserActivity : BaseActivity(), RecyclerAdapter.OnClickEvent {

    private lateinit var adapter: RecyclerAdapter
    var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    lateinit var viewModel: BaseViewModel
    var sortingOrder: String = ""
    private var userId: Int = 0
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        progressBar.visibility = View.VISIBLE







        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Firebase", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                Log.d("Firebase", token.toString())
               // Toast.makeText(baseContext, token.toString(), Toast.LENGTH_SHORT).show()

                //send token
                val client = OkHttpClient()
                val body: RequestBody = FormBody.Builder()
                    .add("Token", token)
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

            })




        viewModel = getViewModel()
        val intent = intent
        val name = intent.getStringExtra("name")
        val lastname = intent.getStringExtra("lastname")
        userId = intent.getStringExtra("id").toInt()

        tvWelcomeMsg.text = "$name  $lastname"
        sortingOrder = sharedPreferences!!.get(SORTING_ORDER, "0")

        adapter = RecyclerAdapter(context = this, onClickEvent = this)
        adapter.setArrayList(ArrayList())
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        viewModel.getEvents(userId)
        dialog = createDialog()


        addEvent.setOnClickListener {
            createEvent()
        }

        viewModel.createEventLiveData.observe(this, Observer {
            when (it) {
                is NetworkResponse.Success -> {
                    sharedPreferences!!.put(SORTING_ORDER, sortingOrder + 1)
                    viewModel.getEvents(userId)
                    dialog.dismiss()
                }
                is NetworkResponse.Error -> {
                    toast(it.message)
                    dialog.dismiss()
                }
            }
        })


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

    private fun createEvent() {
        val dialogButtonOK =
            dialog.findViewById<Button>(R.id.save_form_bt_OK)
        val dialogEtName = dialog.findViewById<EditText>(R.id.save_form_et_name)
        val dialogDescription = dialog.findViewById<EditText>(R.id.save_form_et_description)
        var dialogProgress = dialog.findViewById<ProgressBar>(R.id.dialogProgress)
        dialog.show()
        dialogEtName.text.clear()

        showKeyboard(dialogEtName, null)


        dialogButtonOK.setOnClickListener {
            if (preventMultiClick()) {
                return@setOnClickListener
            }
            if (dialogEtName.isEmpty()) {
                toast("Заполните название:")
                return@setOnClickListener
            }
            viewModel.createEvent(
                    name = dialogEtName.text.toString(),
                    description = dialogDescription.text.toString(),
                    id = userId, time = getCurrentTime(), sortOrder = sortingOrder.toInt())
        }

    }


    override fun onRecyclerClick(event: Event, position: Int) {
        val dialogButtonOK =
            dialog.findViewById<Button>(R.id.save_form_bt_OK)
        val dialogEtName = dialog.findViewById<EditText>(R.id.save_form_et_name)
        val dialogDescription = dialog.findViewById<EditText>(R.id.save_form_et_description)
        var dialogProgress = dialog.findViewById<ProgressBar>(R.id.dialogProgress)
        dialog.show()

        dialogEtName.setText(event.name)
        dialogDescription.setText(event.description)
        showKeyboard(dialogEtName, true)

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

    private fun createDialog(): Dialog {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.save_form)
        dialog.setTitle("Введите название:")
        dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
                                 )
        return dialog
    }

    private fun showKeyboard(dialogEtName: EditText, setSelection: Boolean?) {
        dialogEtName.post {
            val inputMethodManager: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInputFromWindow(
                    dialogEtName.applicationWindowToken, InputMethodManager.SHOW_IMPLICIT, 0
                                                        )
            dialogEtName.requestFocus()
            if (setSelection != null) {
                dialogEtName.setSelection(dialogEtName.text.length)
            }

        }
    }


}
