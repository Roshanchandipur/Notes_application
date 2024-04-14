package com.example.mynotes

import androidx.lifecycle.LiveData
import androidx.room.Update

class NotesRepo(private val notesDao: NotesDAO) {

    val allNotes: LiveData<List<Notes>> = notesDao.getNotesLiveData()

    suspend fun insert(note: Notes){
        val n = HashSet<String>()
        val l = getNotes()
        for(n1 in l)
            n.add(n1.notes)
        if(!n.contains(note.notes))
            notesDao.insertIntoNotes(note)
    }
    suspend fun delete(note: Notes){
        notesDao.deleteNotes(note)
    }

    @Update
    suspend fun update(note: Notes){
        val n = HashSet<String>()
        val l = getNotes()
        for(n1 in l)
            n.add(n1.notes)
        if(!n.contains(note.notes))
            notesDao.updateNote(note)
    }
    suspend fun getNotes(): List<Notes>{
        return notesDao.getNotesList()
    }
}