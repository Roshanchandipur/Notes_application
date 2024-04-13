package com.example.mynotes

import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log

class DataRepo(notesDao: NotesDAO) {

    val notesRepo = NotesRepo(notesDao)
    val dao = DAO_()

    suspend fun uploadToCloud(){
        val notes: List<Notes> = notesRepo.getNotes()
        dao.uploadToCloud(notes)
    }

    fun deleteFromCloud(){
        dao.deleteNotes()
    }

    suspend fun syncWithCloud(){
        Log.d("cloud started", "insider sync with cloud")

        var list = ArrayList<Notes>()

        dao.getNotesFromCloudDB { notes ->
            list.add(Notes(notes.toString()))
            Log.d("cloud started", "adding notes to list")
            Log.d("cloud started", "list size = "+list.size.toString())
        }
        for(n in list){
            notesRepo.insert(n)
        }
        Log.d("cloud started", "list size = " + list.size)
    }
}