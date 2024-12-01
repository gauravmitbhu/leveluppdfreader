package `in`.levelup.pdfreader.data.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import `in`.levelup.pdfreader.model.Pdf
import `in`.levelup.pdfreader.model.PdfText
import `in`.levelup.pdfreader.model.PdfsWithText
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfTextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPdf(pdf: Pdf)

    @Query("DELETE FROM pdf WHERE pdfId = :pdfId")
    suspend fun deletePdf(pdfId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPdfText(pdfText: PdfText)

    @Query("SELECT * FROM pdf")
    fun getAllPdf(): Flow<List<Pdf>>

    @Query("SELECT * FROM pdf ORDER BY pdfid DESC LIMIT 1")
    suspend fun getLatestPdf(): Pdf

    @Transaction
    @Query("SELECT * FROM pdf WHERE pdfId = :pdfId")
    suspend fun getPdfWithText(pdfId: Int): List<PdfsWithText>
}