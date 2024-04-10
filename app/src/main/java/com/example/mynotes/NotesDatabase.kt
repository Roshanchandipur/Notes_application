package com.example.mynotes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(Notes::class), version = 4)

public abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDAO(): NotesDAO

    companion object{
        @Volatile
        var INSTANCE : NotesDatabase? = null

        fun getDatabaseInstance(context: Context): NotesDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, "com.example.mynotes.NotesDatabase").addMigrations(
                    MIGRATION_3_4).build()
                INSTANCE = instance
                instance
            }
        }



        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Copy existing data to new schema
                database.execSQL("CREATE TABLE IF NOT EXISTS `notes_table_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `notes` TEXT NOT NULL)")

                // Copy existing data to the new table
                database.execSQL("INSERT INTO `notes_table_new` (`notes`) SELECT `notes` FROM `notes_table`")
                // Drop the old table
                database.execSQL("DROP TABLE `notes_table`")
                // Rename the new table to the original name
                database.execSQL("ALTER TABLE `notes_table_new` RENAME TO `notes_table`")
            }
        }

    }

}