package `in`.levelup.pdfreader.data.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import `in`.levelup.pdfreader.model.PdfText
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfTextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pdfText: PdfText)

    @Query("SELECT * FROM pdf_text_table ORDER BY pageNumber ASC")
    fun getAllPdfText(): Flow<List<PdfText>>

    @Query("DELETE FROM pdf_text_table")
    suspend fun deleteAllPdfText()
}
