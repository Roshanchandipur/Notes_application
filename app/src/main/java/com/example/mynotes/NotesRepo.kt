package com.example.mynotes

import androidx.lifecycle.LiveData

class NotesRepo(private val notesDao: NotesDAO) {

    val allNotes: LiveData<List<Notes>> = notesDao.getNotes()

    suspend fun insert(note: Notes){
        notesDao.insertIntoNotes(note)
    }
    suspend fun delete(note: Notes){
        notesDao.deleteNotes(note)
    }

}