package com.android.todohelper

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.todohelper.data.Event
import com.android.todohelper.dragAndDrop.SimpleItemTouchHelperCallback
import com.android.todohelper.retrofit.NetworkResponse
import com.android.todohelper.utils.toast
import kotlinx.android.synthetic.main.activity_user.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class UserActivity : AppCompatActivity(), RecyclerAdapter.AdapterCallback {

    lateinit var adapter: RecyclerAdapter
    var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        var viewModel: BaseViewModel = getViewModel()


        val intent = intent
        val name = intent.getStringExtra("name")
        val lastname = intent.getStringExtra("lastname")
        var userId: Int = intent.getStringExtra("id").toInt()

        tvWelcomeMsg.text = "$name  $lastname"


        adapter = RecyclerAdapter(context = this)
        adapter.setArrayList(ArrayList())
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = adapter
        adapter.setAdapterCallback(this)
        recyclerView.layoutManager = layoutManager

        viewModel.getEvents(userId)



        viewModel.getEvents(userId).observe(this, Observer {
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


    }

    override fun readEvents() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
