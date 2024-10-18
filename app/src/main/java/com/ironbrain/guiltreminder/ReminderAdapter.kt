package com.ironbrain.guiltreminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(val activity: OverviewActivity) : RecyclerView.Adapter<ReminderViewHolder>() {
    private val TAG = javaClass.simpleName
    private var mAllReminders: Array<com.ironbrain.guiltreminder.database.Reminder>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.reminder_view, parent, false)
        return ReminderViewHolder(activity, view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(mAllReminders!![position])
    }

    override fun getItemCount(): Int {
        return if (mAllReminders == null) 0 else mAllReminders!!.size
    }

    fun setmAllReminders(reminders: Array<com.ironbrain.guiltreminder.database.Reminder>?) {
        reminders?.sortBy { it.remind_after }
        mAllReminders = reminders
        notifyDataSetChanged()
    }
}