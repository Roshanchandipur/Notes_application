package com.example.mynotes

import androidx.lifecycle.LiveData
import androidx.room.Update

class NotesRepo(private val notesDao: NotesDAO) {

    val allNotes: LiveData<List<Notes>> = notesDao.getNotesLiveData()

    suspend fun insert(note: Notes){
        notesDao.insertIntoNotes(note)
    }
    suspend fun delete(note: Notes){
        notesDao.deleteNotes(note)
    }

    @Update
    suspend fun update(note: Notes){
        notesDao.updateNote(note);
    }
    suspend fun getNotes(): List<Notes>{
        return notesDao.getNotesList()
    }
}