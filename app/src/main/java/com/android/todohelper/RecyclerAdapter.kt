package com.android.todohelper
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.todohelper.data.Event
import com.android.todohelper.dragAndDrop.ItemTouchHelperAdapter
import com.android.todohelper.retrofit.Repository
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*


class RecyclerAdapter(var context: Context) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(), ItemTouchHelperAdapter, KoinComponent {

    private lateinit var eventsList: ArrayList<Event>
    private lateinit var adapterCallback: AdapterCallback
    var repository: Repository = get()

    fun setAdapterCallback(callback: AdapterCallback) {
        adapterCallback = callback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_event, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event: Event = eventsList.get(position)
        viewHolder.name?.text = event.name
        viewHolder.description?.text = event.description
        viewHolder.time?.text = event.time


    }

    override fun getItemCount() = eventsList.size
    fun setArrayList(arrayList: ArrayList<Event>) {
        eventsList = arrayList
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var name: TextView? = null
        var description: TextView? = null
        var time: TextView? = null
        var cardView: CardView? = null

        init {
            // v.setOnClickListener { Log.d(TAG, "Element $adapterPosition clicked.") }
            name = v.findViewById(R.id.tv_name)
            description = v.findViewById(R.id.tv_description)
            time = v.findViewById(R.id.tv_time)
            cardView = v.findViewById(R.id.cardView)
        }
    }

    override fun onItemDismiss(position: Int) {
        val event: Event = eventsList[position]
        deleteItem(event, position)
    }

    private fun deleteItem(event: Event, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Вы уверены?")
            .setCancelable(false)
            .setNegativeButton(
                "NO"
            ) { dialog, _ ->
                dialog.cancel()
                notifyDataSetChanged()
            }.setPositiveButton(
                "YES"
            ) { _, _ ->
                eventsList.removeAt(position)
                notifyItemRemoved(position)
                //delete
                repository.deleteEvent(event.eventId)

            }
        val alert = builder.create()
        alert.show()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val targetOrder: Int = if (fromPosition - toPosition > 0) {
            //moveDown
            eventsList[toPosition].sortOrder - 1
        } else {
            //moveUp
            eventsList[toPosition].sortOrder + 1
        }
        val id: Int = eventsList[fromPosition].eventId
        repository.changeOrder(id, targetOrder)
        Collections.swap(eventsList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    interface AdapterCallback {
        fun readEvents()
    }
}


