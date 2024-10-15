package com.example.guiltreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class NewReminderDialogFragment(private val activity: OverviewActivity) : DialogFragment() {

    @OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                var description by remember { mutableStateOf<String?>(null) }
                var date by remember { mutableStateOf<Long?>(null) }
                var time by remember { mutableStateOf<String?>(null) }

                var showDescriptionInput by remember { mutableStateOf(true) }
                var showDatepicker by remember { mutableStateOf(false) }
                var showTimepicker by remember { mutableStateOf(false) }

                @Composable
                fun DescriptionField() {
                    val textState = remember { mutableStateOf(TextFieldValue()) }
                    val textFieldContentDescription = "Description"
                    TextField(
                        value = textState.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = textFieldContentDescription },
                        onValueChange = {
                            textState.value = it
                            description = textState.value.text
                        },
                    )
                }

                @Composable
                fun DescriptionDialog(
                    onConfirmation: () -> Unit,
                ) {
                    AlertDialog(
                        title = {
                            Text(text = "Enter a description")
                        },
                        text = {
                            DescriptionField()
                        },
                        onDismissRequest = {
                            dismiss()
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onConfirmation()
                                }
                            ) {
                                Text("Next")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    dismiss()
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                @OptIn(ExperimentalMaterial3Api::class)
                @Composable
                fun DatePickerModal(
                    onConfirm: () -> Unit,
                    ) {
                    val datePickerState = rememberDatePickerState()

                    DatePickerDialog(
                        onDismissRequest = {dismiss()},
                        confirmButton = {
                            TextButton(onClick = {
                                date = datePickerState.selectedDateMillis
                                onConfirm()
                            }) {
                                Text("Next")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {dismiss()}) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                @Composable
                fun TimePickerDialog(
                    onConfirm: () -> Unit,
                ) {
                    val currentTime = Calendar.getInstance()

                    val timePickerState = rememberTimePickerState(
                        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
                        initialMinute = currentTime.get(Calendar.MINUTE),
                        is24Hour = true,
                    )

                    Column {
                        TimePicker(
                            state = timePickerState,
                        )
                        Button(onClick = {dismiss()}) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            time = timePickerState.hour.toString() + ":" + timePickerState.minute.toString()
                            onConfirm()
                        }) {
                            Text("Confirm")
                        }
                    }
                }

                if (showDescriptionInput) {
                    DescriptionDialog(
                        onConfirmation = {
                            showDescriptionInput = false;
                            showDatepicker = true;
                        },
                    )
                }

                if (showDatepicker) {
                    DatePickerModal(
                        onConfirm = {
                            showDatepicker = false;
                            showTimepicker = true
                        },
                    )
                }

                if (showTimepicker) {
                    TimePickerDialog(
                        onConfirm = {
                            var timestamp: String = "";
                            if (date !== null) {
                                timestamp = SimpleDateFormat("yyyy/MM/dd").format(Date(date!!));
                            }

                            timestamp += " $time:00";

                            GlobalScope.launch {
                                activity.addReminder(description!!, timestamp)
                            }
                            dismiss();
                        }
                    )
                }
            }
        }
    }
}