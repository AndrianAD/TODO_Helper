package com.android.todohelper

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.todohelper.data.Event
import com.android.todohelper.dragAndDrop.ItemTouchHelperAdapter
import com.android.todohelper.retrofit.Repository
import com.android.todohelper.utils.toast
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*


class RecyclerAdapter(var context: Context, var onClickEvent: OnClickEvent) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(), ItemTouchHelperAdapter, KoinComponent {

    private lateinit var eventsList: ArrayList<Event>
    var repository: Repository = get()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_event, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event: Event = eventsList[position]
        viewHolder.name?.text = event.name
        viewHolder.description?.text = event.description
        viewHolder.time?.text = event.time
        viewHolder.cardView!!.setOnClickListener { onClickEvent.onRecyclerClick(event, position) }

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

    override fun onSwipeRight(position: Int) {
        val event: Event = eventsList[position]
        deleteItem(event, position)
    }

    private fun deleteItem(event: Event, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Вы уверены что хотите удалить?")
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
                repository.deleteEvent(event.eventId)
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val targetOrder: Int = if (fromPosition - toPosition > 0) {
            //moveDown
            eventsList[toPosition].sortOrder - 1
        }
        else {
            //moveUp
            eventsList[toPosition].sortOrder + 1
        }
        val id: Int = eventsList[fromPosition].eventId
        repository.changeOrder(id, targetOrder)
        Collections.swap(eventsList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwipeLeft(position: Int) {

        val event: Event = eventsList[position]
        SingleDateAndTimePickerDialog.Builder(context)
            .curved()
            .displayListener { notifyDataSetChanged() }
            .minutesStep(1)
            .title("Simple")
            .listener { date ->
                context.toast( date.toString())
                val alarm =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val startTime = Calendar.getInstance()
                startTime[Calendar.HOUR_OF_DAY] = date.hours
                startTime[Calendar.MINUTE] = date.minutes
                startTime[Calendar.DATE] = date.date
                val intent = Intent(context, AlarmReceiver::class.java)
                intent.putExtra("notificationId", Random().nextInt(100).toString())
                intent.putExtra("title", event.name)
                intent.putExtra("description", event.description)
                val alarmIntent = PendingIntent.getBroadcast(
                        context, Random().nextInt(1000),
                        intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmStartTime = startTime.timeInMillis
                alarm[AlarmManager.RTC_WAKEUP, alarmStartTime] = alarmIntent
            }.display()

    }


    interface OnClickEvent {
        fun onRecyclerClick(event: Event, position: Int)
    }
}


