package com.ironbrain.guiltreminder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "remind_after") val remind_after: Date,
    @ColumnInfo(name = "last_reminded") val last_reminded: Date? = null
)
