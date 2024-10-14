package com.example.guiltreminder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Modifier
import java.util.Date

class OverviewActivity : AppCompatActivity() {
    private lateinit var mReminderRecyclerView: RecyclerView
    private lateinit var mReminderAdapter: ReminderAdapter
    private val myLive = MutableLiveData<String>()

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

        val newReminderButton: ImageButton = findViewById(R.id.newReminderButton)
        newReminderButton.setOnClickListener(View.OnClickListener { view: View ->
            val newFragment = NewReminderDialogFragment(this)
            newFragment.show(supportFragmentManager, "game")
        })
    }

    public fun addReminder(description: String, timestamp: String) {
        Log.i("description", description);
        Log.i("timestamp", timestamp);
    }

}