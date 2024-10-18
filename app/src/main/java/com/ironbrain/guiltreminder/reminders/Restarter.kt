package com.ironbrain.guiltreminder.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class Restarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val intent: Intent = Intent(context, ReminderService::class.java);
        context.startService(intent)
    }
}