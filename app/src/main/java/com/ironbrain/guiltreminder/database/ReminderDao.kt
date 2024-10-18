package com.ironbrain.guiltreminder.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder ORDER BY remind_after ASC")
    fun getAll(): List<Reminder>

    @Query("SELECT * FROM reminder WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Reminder>

    @Insert
    fun insertAll(vararg reminders: Reminder)

    @Query("UPDATE reminder SET last_reminded = :lastReminded WHERE id = :id")
    fun updateLastReminded(id: Int, lastReminded: Date): Int

    @Delete
    fun delete(reminder: Reminder)

    @Query("DELETE FROM reminder")
    fun delete_all()
}