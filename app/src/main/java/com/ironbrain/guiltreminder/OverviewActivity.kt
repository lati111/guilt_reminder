package com.ironbrain.guiltreminder

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ironbrain.guiltreminder.database.AppDatabase
import com.ironbrain.guiltreminder.database.Reminder
import com.ironbrain.guiltreminder.database.ReminderDao
import com.ironbrain.guiltreminder.reminders.ReminderService
import com.ironbrain.guiltreminder.reminders.Restarter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Objects

class OverviewActivity : AppCompatActivity() {
    lateinit var mServiceIntent: Intent;
    private lateinit var mReminderService: ReminderService;

    private lateinit var mReminderRecyclerView: RecyclerView
    private lateinit var mReminderAdapter: ReminderAdapter
    private lateinit var mReminderDao: ReminderDao

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_overview)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mReminderAdapter = ReminderAdapter(this)
        mReminderRecyclerView = findViewById(R.id.reminderRecyclerView)
        mReminderRecyclerView.adapter = mReminderAdapter

        this.mReminderDao = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "guilt-reminder-database"
        ).fallbackToDestructiveMigration().build().reminderDao();

        val newReminderButton: ImageButton = findViewById(R.id.newReminderButton)
        newReminderButton.setOnClickListener(View.OnClickListener { view: View ->
            val newFragment = NewReminderDialogFragment(this)
            newFragment.show(supportFragmentManager, "game")
        })

        GlobalScope.launch {
            loadReminders()
        }

        try {
            initReminderService()
        } catch (exception: Exception) {

        }
    }

    override fun onDestroy() {
        // Restart reminder service as background thread
        val restartIntent: Intent = Intent();
        restartIntent.setAction("restartservice");
        restartIntent.setClass(this, Restarter::class.java);
        this.sendBroadcast(restartIntent);

        super.onDestroy()
    }

    fun loadReminders() {
        val reminders = this.mReminderDao.getAll().toTypedArray();
        runOnUiThread {
            mReminderAdapter.setmAllReminders(reminders);
        }
    }

    fun addReminder(description: String, timestamp: String) {
        this.mReminderDao.insertAll(
            Reminder(0, description, Date(timestamp))
        )

        loadReminders();
    }

    fun finishReminder(reminder: Reminder) {
        this.mReminderDao.delete(reminder)

        loadReminders();
    }

    fun initReminderService() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(
                this@OverviewActivity,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions: Array<String> =arrayOf(Manifest.permission.POST_NOTIFICATIONS.toString())
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        // Create notification channel
        val mChannel = NotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        mChannel.description = getString(R.string.notification_channel_description)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        // Launch service
        mReminderService = ReminderService();
        mServiceIntent = Intent(this, ReminderService::class.java);
        if (!isReminderServiceRunning(ReminderService::class.java)) {
            startService(mServiceIntent);
        }
        startService(Intent(this, ReminderService::class.java))
    }

    private fun isReminderServiceRunning(serviceClass: Class<ReminderService>): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningServices: List<ActivityManager.RunningServiceInfo> = Objects.requireNonNull(activityManager).getRunningServices(Int.MAX_VALUE)
        for (i in runningServices.indices) {
            if (serviceClass.getName().equals(runningServices[i].service.className)) {
                return true;
            }
        }

        return false;
    }
}