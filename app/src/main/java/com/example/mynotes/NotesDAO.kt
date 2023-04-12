package com.example.mynotes

import androidx.lifecycle.LiveData
import androidx.room.*
import java.nio.charset.CodingErrorAction.IGNORE

@Dao

interface NotesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntoNotes(notes: Notes)

    @Delete
    suspend fun deleteNotes(notes: Notes)

    @Query("SELECT * FROM notes_table")
    fun getNotes() : LiveData<List<Notes>>

}