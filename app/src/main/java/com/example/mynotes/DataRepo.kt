package com.example.mynotes

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataRepo(notesDao: NotesDAO) {

    val notesRepo = NotesRepo(notesDao)
    val dao = DAO_()

    suspend fun uploadToCloud() {
        val notes: List<Notes> = notesRepo.getNotes()
        dao.uploadToCloud(notes)
    }

    fun deleteFromCloud() {
        dao.deleteNotes()
        print("Deleted from cloud")
    }

    suspend fun syncWithCloud() {
        Log.d("cloud started", "insider sync with cloud")
        var list = ArrayList<Notes>()

        dao.getNotesFromCloudDB { notes ->
            for(item in notes)
                list.add(Notes(item))
            Log.d("cloud started", "adding notes to list")
            Log.d("cloud started", "list size = " + list.size.toString())
            GlobalScope.launch(Dispatchers.IO) {
                val n: List<Notes> = notesRepo.getNotes()
                val l = HashSet<String>()
                for(i in n){
                    l.add(i.notes)
                }
                for (i in list) {
                    if(l.contains(i.notes)) continue
                    notesRepo.insert(i)
                }
                print("You have successfully cloned the cloud data")
            }
        }
    }
}
