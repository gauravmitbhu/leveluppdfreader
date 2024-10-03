package `in`.levelup.pdfreader.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.levelup.pdfreader.repository.Repository
import `in`.levelup.pdfreader.tts.TTSManager
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRepository() = Repository()

    @Provides
    @Singleton
    fun provideTTSManager(@ApplicationContext context: Context): TTSManager {
        return TTSManager(context)
    }

}