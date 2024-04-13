package com.example.mynotes

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DAO_() {

    private var firebaseDatabase: FirebaseDatabase
    private var user: FirebaseUser

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
    }

    fun uploadToCloud(list: List<Notes>) {

        var l = ArrayList<String>()

        list.forEach{
            n-> l.add(n.notes)
        }
        Log.d("listLength", l.size.toString())
        if(l.size==0)
            l.add("nothing is there")

        firebaseDatabase.getReference().child("users").child(user.uid).child("notes")
            .setValue(l)
    }

    fun deleteNotes() {
        firebaseDatabase.getReference().child("users").child(user.getUid()).child("notes")
            .removeValue()
    }

    fun getNotesFromCloudDB(callback: (List<String>)-> Unit){
        val n = firebaseDatabase.getReference().child("users").child(user.uid).child("notes")
        val valueEventListener = object : ValueEventListener {
            var notes = ArrayList<String>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    if (data != null)
                        notes.add(data.getValue(String::class.java)!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // update UI for error
            }
        }
        n.addListenerForSingleValueEvent(valueEventListener)
    }
}