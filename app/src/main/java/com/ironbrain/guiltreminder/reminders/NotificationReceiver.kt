package com.ironbrain.guiltreminder.reminders

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.ironbrain.guiltreminder.OverviewActivity
import com.ironbrain.guiltreminder.R
import com.ironbrain.guiltreminder.database.AppDatabase
import com.ironbrain.guiltreminder.database.Reminder
import com.ironbrain.guiltreminder.database.ReminderDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class NotificationReceiver : BroadcastReceiver() {
    private lateinit var mReminderDao: ReminderDao

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminderId", 0)

        GlobalScope.launch {
            val reminder = getReminder(context, reminderId);
            if (reminder !== null) {
                sendReminder(context, reminder)
                scheduleNextNotification(context, reminderId);
            }
        }
    }

    fun sendReminder(context: Context, reminder: Reminder) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var builder = NotificationCompat.Builder(context.applicationContext, context.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.appicon)
            .setContentTitle("Reminder: "+reminder.description)
            .setContentText(context.getString(R.string.notification_channel_description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationIntent: Intent = Intent(context, OverviewActivity::class.java);
        val conPendingIntent: PendingIntent = PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(conPendingIntent);

        val notifId = (reminder.id.toString() + "00" + reminder.remind_count.toString()).toInt()
        notificationManager.notify(notifId, builder.build())
    }

    fun scheduleNextNotification(context: Context, reminderId: Int) {
        this.mReminderDao.updateLastReminded(reminderId, Date());
        val reminder = this.getReminder(context, reminderId)
        if (reminder === null) {
            return;
        }

        // Schedule next reminder
        ReminderManager.sendReminder(context, reminder);
    }

    fun getReminder(context: Context, reminderId: Int): Reminder? {
        this.mReminderDao = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "guilt-reminder-database"
        ).fallbackToDestructiveMigration().build().reminderDao();

        return this.mReminderDao.get(reminderId);
    }
}