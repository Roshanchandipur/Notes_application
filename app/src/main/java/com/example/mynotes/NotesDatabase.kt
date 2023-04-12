package com.example.mynotes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Notes::class), version = 1)

public abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDAO(): NotesDAO

    companion object{
        @Volatile
        var INSTANCE : NotesDatabase? = null

        fun getDatabaseInstance(context: Context): NotesDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, "com.example.mynotes.NotesDatabase").build()
                INSTANCE = instance
                instance
            }
        }
    }

}