package `in`.levelup.pdfreader.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.levelup.pdfreader.data.roomdatabase.PdfDatabase
import `in`.levelup.pdfreader.data.roomdatabase.PdfTextDao
import `in`.levelup.pdfreader.repository.Repository
import `in`.levelup.pdfreader.tts.TTSManager
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRepository(pdfTextDao: PdfTextDao) = Repository(pdfTextDao = pdfTextDao)

    @Provides
    @Singleton
    fun provideTTSManager(@ApplicationContext context: Context): TTSManager {
        return TTSManager(context)
    }

    @Singleton
    @Provides
    fun providesPdfDao(pdfDatabase: PdfDatabase): PdfTextDao
            = pdfDatabase.pdfTextDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): PdfDatabase
            = Room.databaseBuilder(
        context,
        PdfDatabase::class.java,
        "pdf_db")
        .fallbackToDestructiveMigration()
        .build()
}