package `in`.levelup.pdfreader.data.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import `in`.levelup.pdfreader.model.Pdf
import `in`.levelup.pdfreader.model.PdfText

@Database(entities = [Pdf::class,PdfText::class], version = 2, exportSchema = false)

abstract class PdfDatabase: RoomDatabase() {
    abstract fun pdfTextDao(): PdfTextDao
}