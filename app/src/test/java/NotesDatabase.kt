import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Notes::class], version = 1, exportSchema = false)

public abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDAO(): NotesDAO

    companion object{
        var INSTANCE : NotesDatabase? = null

        fun getDatabaseInstance(context: Context): NotesDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, NotesDatabase::class.java, "NotesDatabase").build()
                INSTANCE = instance
                instance
            }
        }
    }

}