package `in`.levelup.pdfreader.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "pdf")
data class Pdf(
    @PrimaryKey(autoGenerate = true)
    val pdfId: Int = 0,
    val pdfName: String
)

@Entity(tableName = "pdf_text")
data class PdfText(
    @PrimaryKey(autoGenerate = true)
    val dataId: Int = 0,
    val pageNumber: Int,
    val text: String,
    val pdfId: Int
)

data class PdfsWithText(
    @Embedded val pdf: Pdf,
    @Relation(
        parentColumn = "pdfId",
        entityColumn = "pdfId"
    )
    val pdfTexts: List<PdfText>
)