package com.example.guiltreminder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

class OverviewActivity : AppCompatActivity() {
    private lateinit var mReminderRecyclerView: RecyclerView
    private lateinit var mReminderAdapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_overview)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mReminderAdapter = ReminderAdapter()
        mReminderRecyclerView = findViewById(R.id.reminderRecyclerView)
        mReminderRecyclerView.adapter = mReminderAdapter

        val reminders = arrayOf(
            Reminder("Wash clothes", Date("2024/11/24 19:00")),
            Reminder("Dishes", Date("2024/10/14 16:00")),
            Reminder("Wash clothes", Date("2024/10/24 19:00")),
            Reminder("Wash bed covers", Date("2024/10/15 18:30")),
            Reminder("vacuum", Date("2024/10/17 8:00")),
        );

        mReminderAdapter.setmAllReminders(reminders);

    }
}