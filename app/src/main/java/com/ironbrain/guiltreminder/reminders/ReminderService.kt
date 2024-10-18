package com.ironbrain.guiltreminder.reminders

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.ironbrain.guiltreminder.R
import com.ironbrain.guiltreminder.database.AppDatabase
import com.ironbrain.guiltreminder.database.Reminder
import com.ironbrain.guiltreminder.database.ReminderDao
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Date

class ReminderService : Service() {
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private lateinit var mReminderDao: ReminderDao

    override fun onCreate() {
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            try {
                while(true) {
                    Thread.sleep(60000)
                    checkReminders()
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            stopSelf(msg.arg1)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        this.mReminderDao = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "guilt-reminder-database"
        ).fallbackToDestructiveMigration().build().reminderDao();

        return START_STICKY
    }

    fun checkReminders() {
        val reminders: List<Reminder> = this.mReminderDao.getAll();
        for (reminder in reminders) {
            val currDate: Date = Date();
            if (currDate.after(reminder.remind_after)) {
                val lastRemindedTime: Date? = reminder.last_reminded;
                val comparisonTime: Date = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
                if (reminder.last_reminded === null || comparisonTime.after(lastRemindedTime)) {
                    sendReminder(reminder);
                }
            }
        }
    }

    fun sendReminder(reminder: Reminder) {
        var builder = NotificationCompat.Builder(applicationContext, getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.appicon)
            .setContentTitle("Reminder: " + reminder.description)
            .setContentText("You still have a task to do, now would be a good time to start.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("reminder", "permission missing")
                return
            }

            notify(reminder.id, builder.build());
        }

        this.mReminderDao.updateLastReminded(reminder.id, Date());
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}