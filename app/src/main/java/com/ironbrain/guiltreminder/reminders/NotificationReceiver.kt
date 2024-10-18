package com.ironbrain.guiltreminder.reminders

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.ironbrain.guiltreminder.OverviewActivity
import com.ironbrain.guiltreminder.R
import com.ironbrain.guiltreminder.database.AppDatabase
import com.ironbrain.guiltreminder.database.ReminderDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class NotificationReceiver : BroadcastReceiver() {
    private lateinit var mReminderDao: ReminderDao

    override fun onReceive(context: Context, intent: Intent) {
        // Get data from intent
        val notificationId = intent.getIntExtra("notificationId", 0)
        val reminderId = intent.getIntExtra("reminderId", 0)
        val message = intent.getStringExtra("message")
        val title = intent.getStringExtra("title")

        // Create notification
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var builder = NotificationCompat.Builder(context.applicationContext, context.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.appicon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationIntent: Intent = Intent(context, OverviewActivity::class.java);
        val conPendingIntent: PendingIntent = PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(conPendingIntent);

        notificationManager.notify(notificationId, builder.build())

        // Update database
        GlobalScope.launch {
            scheduleNextNotification(context, reminderId);
        }
    }

    fun scheduleNextNotification(context: Context, reminderId: Int) {
        this.mReminderDao = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "guilt-reminder-database"
        ).fallbackToDestructiveMigration().build().reminderDao();

        this.mReminderDao.updateLastReminded(reminderId, Date());
        val reminder = this.mReminderDao.get(reminderId);

        // Schedule next reminder
        ReminderManager.sendReminder(context, reminder);
    }
}