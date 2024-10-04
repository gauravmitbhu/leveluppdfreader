package `in`.levelup.pdfreader.data.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import `in`.levelup.pdfreader.model.PdfText

@Database(entities = [PdfText::class], version = 1, exportSchema = false)

abstract class PdfDatabase: RoomDatabase() {
    abstract fun pdfTextDao(): PdfTextDao
}