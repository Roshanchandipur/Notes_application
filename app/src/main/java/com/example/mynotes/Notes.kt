package com.example.mynotes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.reflect.KClass

@Entity(tableName = "notes_table")
class Notes(
    @ColumnInfo(name = "notes") var notes: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}