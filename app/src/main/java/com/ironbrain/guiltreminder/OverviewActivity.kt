package com.ironbrain.guiltreminder

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ironbrain.guiltreminder.database.AppDatabase
import com.ironbrain.guiltreminder.database.Reminder
import com.ironbrain.guiltreminder.database.ReminderDao
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class OverviewActivity : AppCompatActivity() {
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
}