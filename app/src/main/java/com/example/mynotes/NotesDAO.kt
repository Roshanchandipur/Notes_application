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

    @Query("SELECT * FROM notes_table ORDER BY id desc")
    fun getNotesLiveData() : LiveData<List<Notes>>

    @Query("SELECT * FROM notes_table ORDER BY id desc")
    suspend fun getNotesList() : List<Notes>

    @Update
    suspend fun updateNote(note: Notes)
}