package com.android.todohelper.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.todohelper.App
import com.android.todohelper.R
import com.android.todohelper.activity.viewModel.BaseViewModel
import com.android.todohelper.adapter.RecyclerAdapter
import com.android.todohelper.data.Event
import com.android.todohelper.dragAndDrop.SimpleItemTouchHelperCallback
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.service.AlarmReceiver
import com.android.todohelper.utils.*
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*
import kotlin.collections.ArrayList

class UserActivity : BaseActivity(),
    RecyclerAdapter.OnClickEvent, FragmentCallbackToActivity {

    lateinit var adapter: RecyclerAdapter
    private var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    lateinit var viewModel: BaseViewModel
    private var sortingOrder: String = ""
    private var userId: Int = 0
    lateinit var dialog: Dialog
    private lateinit var broadCastReceiver: BroadcastReceiver
    var dX: Float = 0.0f
    var dY: Float = 0.0f
    var lastAction: Int = 0
    lateinit var createEvent: FloatingActionButton


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        progressBar.visibility = View.VISIBLE
        dialog = createDialog(R.layout.save_form)

        userId = intent.getStringExtra("id").toInt()


        viewModel = getViewModel()

        broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    BROADCAST_ACTION -> viewModel.getEvents(userId)
                }
            }
        }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadCastReceiver, IntentFilter(BROADCAST_ACTION))

        val intent = intent
        val name = intent.getStringExtra("name")
        val lastName = intent.getStringExtra("lastname")


        tvWelcomeMsg.text = "$name  $lastName"
        sortingOrder = sharedPreferences!!.get(SORTING_ORDER, "0")

        adapter = RecyclerAdapter(
                context = this,
                onClickEvent = this)
        adapter.setArrayList(ArrayList())
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        viewModel.getEvents(userId)

        createEvent = FloatingActionButton(this)


//        val onTouchListener = View.OnTouchListener { v, event ->
//            when (event.actionMasked) {
//                ACTION_DOWN -> {
//                    dX = v.x - event.rawX
//                    dY = v.y - event.rawY
//                    lastAction = ACTION_DOWN
//                }
//                ACTION_MOVE -> {
//                    lastAction = ACTION_MOVE
//                    v.y = event.rawY + dY
//                    v.x = event.rawX + dX
//                    sharedPreferences!!.put(SHARED_POSITION_LOGOUT_BUTTON, "${v.x}!${v.y}")
//                }
//                ACTION_UP -> {
//                    if (lastAction == ACTION_DOWN) {
//                        createEvent()
//                    }
//                }
//                else -> {
//                }
//            }; true
//        }

        createEvent.setOnTouchListener(makeMovebleOnTouchListener(sharedPreferences!!) { createEvent() })

        btLogout.setOnClickListener {

            App.instance.clearPreferances()
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseInstanceId.getInstance().apply {
                    deleteInstanceId()
                    instanceId
                }
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        //--------------------live data observers ---------------------------->

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

        viewModel.addEventToUserLiveData.observe(this, Observer {
            when (it) {
                is NetworkResponse.Success -> {
                    it.output as String
                    toast(it.output)
                }
                is NetworkResponse.Error -> toast(it.message)
            }
        })


        viewModel.deleteEventLiveData.observe(this, Observer {
            when (it) {
                is NetworkResponse.Success -> {
                    toast("deleted")
                }
                is NetworkResponse.Error -> toast(it.message)
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

        //onCreate end .........................................
    }

    override fun onResume() {

        if (intent.getStringExtra("fromWidget") != null) {
            createEvent()
        }


        val rel: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        val widthScreen = size.x
        val heightScreen = size.y

        val xy: String = sharedPreferences!!.get(
                SHARED_POSITION_LOGOUT_BUTTON,
                "${widthScreen - 250}!${heightScreen - 450}")
        val x = xy.takeWhile { it != '!' }.toFloat().toInt()
        val y = xy.takeLastWhile { it != '!' }.toFloat().toInt()
        createEvent.setImageResource(android.R.drawable.ic_input_add)
        createEvent.size = FloatingActionButton.SIZE_NORMAL

        createEvent.x = x.toFloat()
        createEvent.y = y.toFloat()

        if (createEvent.parent == null) {
            frameLayout.addView(createEvent, rel)
        }



        super.onResume()
    }

    private fun createEvent() {
        val dialogButtonOK =
            dialog.findViewById<Button>(R.id.save_form_bt_OK)
        val dialogEtName = dialog.findViewById<EditText>(R.id.save_form_et_name)
        val dialogDescription = dialog.findViewById<EditText>(R.id.save_form_et_description)
        var dialogProgress = dialog.findViewById<ProgressBar>(R.id.dialogProgress)
        dialog.show()
        dialogEtName.text.clear()
        dialogDescription.text.clear()

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
        val dialogRegisterUser = DetailsFragment(event)
        val fragment = supportFragmentManager.findFragmentByTag(DetailsFragment.TAG)
        if (fragment == null) {
            dialogRegisterUser.show(supportFragmentManager, DetailsFragment.TAG)
        }
        else {
            (fragment as DetailsFragment).showsDialog
        }


    }

    override fun onRecyclerLeftSwipe(
        event: Event,
        position: Int,
        viewHolder: RecyclerView.ViewHolder) {

        adapter.notifyDataSetChanged()

        var popup = PopupMenu(this, viewHolder.itemView, Gravity.RIGHT)
        popup.inflate(R.menu.context_menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.newLink -> {
                    val dialog = createDialog(R.layout.dialog_edittext_form)
                    val btnOk = dialog.findViewById<Button>(R.id.OkBnt)
                    val emailET = dialog.findViewById<EditText>(R.id.emailEt)
                    dialog.show()

                    btnOk.setOnClickListener {
                        if (emailET.isEmpty()) {
                            return@setOnClickListener
                        }
                        else {
                            viewModel.addEventToUser(emailET.text.toString(), event.eventId)
                            viewModel.notifyUser(
                                    email = emailET.text.toString(),
                                    message = event.description)
                            dialog.dismiss()
                        }
                    }
                }
                R.id.notification -> {
                    startTimePicker(event)
                }

                R.id.edit -> {
                    editEvent(event)
                }
            }
            true
        })
        popup.show()

    }

    private fun editEvent(event: Event) {
        val dialogButtonOK =
            dialog.findViewById<Button>(R.id.save_form_bt_OK)
        val dialogEtName = dialog.findViewById<EditText>(R.id.save_form_et_name)
        val dialogDescription =
            dialog.findViewById<EditText>(R.id.save_form_et_description)
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

    override fun onRecyclerRightSwipe(
        event: Event,
        position: Int) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Вы уверены что хотите удалить?")
            .setCancelable(false)
            .setNegativeButton(
                    "NO"
                              ) { dialog, _ ->
                dialog.cancel()
                adapter.notifyDataSetChanged()
            }.setPositiveButton(
                    "YES"
                               ) { _, _ ->
                adapter.eventsList.removeAt(position)
                adapter.notifyItemRemoved(position)
                viewModel.deleteEvent(event.eventId)
            }
        val alert = builder.create()
        alert.show()

    }

    private fun startTimePicker(event: Event) {
        SingleDateAndTimePickerDialog.Builder(this)
            .curved()
            .displayListener { adapter.notifyDataSetChanged() }
            .minutesStep(1)
            .title("Simple")
            .listener { date ->
                this.toast(date.toString())
                val alarm =
                    this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val startTime = Calendar.getInstance()
                startTime[Calendar.HOUR_OF_DAY] = date.hours
                startTime[Calendar.MINUTE] = date.minutes
                startTime[Calendar.DATE] = date.date
                val intent = Intent(this, AlarmReceiver::class.java)
                intent.putExtra("notificationId", Random().nextInt(100).toString())
                intent.putExtra("title", event.name)
                intent.putExtra("description", event.description)
                val alarmIntent = PendingIntent.getBroadcast(
                        this, Random().nextInt(1000),
                        intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmStartTime = startTime.timeInMillis
                alarm[AlarmManager.RTC_WAKEUP, alarmStartTime] = alarmIntent
            }.display()
    }

    private fun createDialog(layout: Int): Dialog {
        val dialog = Dialog(this)
        dialog.setContentView(layout)
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

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)
        super.onDestroy()
    }

    override fun fragmentCallbackToActivity(event: Event) {
        // editEvent(event)
        var ss = "Andrian"
        toast(ss.toByte().toString())
    }
}






