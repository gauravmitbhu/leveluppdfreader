package `in`.levelup.pdfreader.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_text_table")
data class PdfText(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pageNumber: Int,
    val text: String
)