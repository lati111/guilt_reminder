package com.example.guiltreminder

import android.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.fragment.app.DialogFragment
import com.example.guiltreminder.database.Reminder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class FinishReminderDialogFragment(val activity: OverviewActivity, val reminder: Reminder): DialogFragment() {

    @OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                var lockButtons by remember { mutableStateOf(true) }
                var state by remember { mutableIntStateOf(0) }

                @Composable
                fun Dialog(
                    dialogTitle: String,
                    dialogText: String,
                    confirmText: String,
                    dismissText: String,
                    onFirstButtonClick: () -> Unit,
                    onSecondButtonClick: () -> Unit,
                    ) {
                    AlertDialog(

                        title = {
                            Text(text = dialogTitle)
                        },
                        text = {
                            Text(text = dialogText)
                        },
                        onDismissRequest = {
                            dismiss()
                        },
                        confirmButton = {
                            TextButton(
                                colors = if (!lockButtons) ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = colorResource(R.color.holo_purple)) else ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray),
                                onClick = {
                                    if (lockButtons === false) {
                                        onSecondButtonClick()
                                    }
                                }
                            ) {
                                Text(confirmText)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                colors = if (!lockButtons) ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = colorResource(R.color.holo_purple)) else ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray),
                                onClick = {
                                    if (lockButtons === false) {
                                        onFirstButtonClick()
                                    }
                                }
                            ) {
                                Text(dismissText)
                            }
                        }
                    )
                }

                suspend fun unlockButtons() {
                    delay(2000)
                    lockButtons = false
                    Log.i("lock", "buttons unlocked")
                }

                fun lockButtons() {
                    lockButtons = true
                    GlobalScope.launch {
                        unlockButtons()
                    }

                    Log.i("lock", "buttons locked")
                }

                lockButtons();

                if (state === 0) {
                    Dialog(
                        "Have you finished this task?",
                        "Do dishes",
                        "Yes, I have",
                        "Not yet",
                        {
                            dismiss()
                        },
                        {
                            lockButtons()
                            state = 1;
                        }
                    )
                }

                if (state === 1) {
                    Dialog(
                        "Are you sure?",
                        "If not, that's okay. You can just do it right now and be guilt free!",
                        "I'll go do it right now",
                        "Yes, I'm sure",
                        {
                            lockButtons()
                            state = 2;
                        },
                        {
                            dismiss()
                        }
                        )
                }

                if (state === 2) {
                    Dialog(
                        "Are you lying to yourself?",
                        "It happens. Removing the annoying reminder is tempting, even if you haven't done the task yet, but the task still needs to be done. Why not do it right now? I know you've got this. You're stronger than this stupid little task. ",
                        "Sorry, I lied. I'll do it now",
                        "I really did it, I promise",
                        {
                            lockButtons()
                            state = 3;
                        },
                        {
                            dismiss();
                        }
                    )
                }

                if (state === 3) {
                    Dialog(
                        "Task completed",
                        "Good job on doing task! Go get that dopamine hit as a reward.",
                        "Thanks!",
                        "Wait go back, I lied",
                        {
                            dismiss();
                        },
                        {
                            GlobalScope.launch {
                                activity.finishReminder(reminder)
                            }

                            dismiss();
                        }
                    )
                }
            }
        }
    }
}