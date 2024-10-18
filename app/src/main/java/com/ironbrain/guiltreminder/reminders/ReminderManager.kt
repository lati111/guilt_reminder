package com.ironbrain.guiltreminder.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import com.ironbrain.guiltreminder.database.Reminder
import java.util.Calendar

class ReminderManager {
    companion object {
        fun sendReminder(context: Context, reminder: Reminder) {
            val calendar = Calendar.getInstance();
            if (reminder.remind_after.after(calendar.time)) {
                // Set remind time to due date
                calendar.setTime(reminder.remind_after);
            } else {
                // Set remind time to in 5 minutes
                calendar.add(Calendar.MINUTE, 5);
            }

            calendar.set(Calendar.SECOND, 0);

            // Create notification intent
            val notificationIntent = Intent(context, NotificationReceiver::class.java)
            notificationIntent.putExtra("notificationId", 1)
            notificationIntent.putExtra("reminderId", reminder.id)
            notificationIntent.putExtra("title", "Reminder: "+reminder.description)
            notificationIntent.putExtra("message", "You still have a task to do, now would be a good time to start. You got this!")

            // Make intent pending
            val pendingIntent = PendingIntent.getBroadcast(
                context.applicationContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

            // Add as alarm
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

}