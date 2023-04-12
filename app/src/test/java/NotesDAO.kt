import androidx.lifecycle.LiveData
import androidx.room.*

@Dao

interface NotesDAO {

    @Insert
    suspend fun insertIntoNotes(notes: Notes)

    @Update
    suspend fun updateNotes(notes: Notes)

    @Delete
    suspend fun deleteNotes(notes: Notes)

    @Query("SELECT * FROM Notes_Table")
    fun getNotes() : LiveData<List<Notes>>

}