package com.example.mynotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(application: Application): AndroidViewModel(application) {

    val allNotes: LiveData<List<Notes>>
    val repo: NotesRepo

    fun insert(note: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(note)
    }

    fun change(note: Notes) = viewModelScope.launch(Dispatchers.IO){
        repo.delete(note)
    }

    fun edit(note: Notes) = viewModelScope.launch(Dispatchers.IO){
        repo.update(note)
    }
    suspend fun getAllNotes(): List<Notes>{
        return repo.getNotes()
    }

    init {
        val database = NotesDatabase.getDatabaseInstance(application)
        val noteDao = database.notesDAO()
        repo = NotesRepo(noteDao)
        allNotes = repo.allNotes
    }
}