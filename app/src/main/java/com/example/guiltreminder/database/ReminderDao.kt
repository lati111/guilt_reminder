package com.example.guiltreminder.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder ORDER BY remind_after ASC")
    fun getAll(): List<Reminder>

    @Query("SELECT * FROM reminder WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Reminder>

    @Insert
    fun insertAll(vararg reminders: Reminder)

    @Delete
    fun delete(reminder: Reminder)

    @Query("DELETE FROM reminder")
    fun delete_all()
}