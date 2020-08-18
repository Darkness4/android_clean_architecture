package marc.nguyen.cleanarchitecture.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import marc.nguyen.cleanarchitecture.data.database.DatabaseManager
import marc.nguyen.cleanarchitecture.data.database.RepoDao
import marc.nguyen.cleanarchitecture.data.datasources.GithubDataSource
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideGithubDataSource(client: OkHttpClient): GithubDataSource {
        return Retrofit.Builder()
            .baseUrl(GithubDataSource.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(GithubDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient() = OkHttpClient()

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): DatabaseManager {
        return Room.databaseBuilder(
            context,
            DatabaseManager::class.java,
            "clean_architecture.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRepoDao(database: DatabaseManager): RepoDao {
        return database.repoDao()
    }
}
