import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.reflect.KClass

@Entity(tableName = "notes_table")
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val notes: String

)