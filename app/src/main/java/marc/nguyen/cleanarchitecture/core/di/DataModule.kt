package marc.nguyen.cleanarchitecture.core.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import marc.nguyen.cleanarchitecture.data.database.DatabaseManager
import marc.nguyen.cleanarchitecture.data.database.RepoDao
import marc.nguyen.cleanarchitecture.data.datasources.GithubDataSource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DataModule {
    @ExperimentalSerializationApi
    @Provides
    @Singleton
    fun provideGithubDataSource(client: Lazy<OkHttpClient>): GithubDataSource {
        return Retrofit.Builder()
            .baseUrl(GithubDataSource.BASE_URL)
            .client(client.get())
            .addConverterFactory(
                Json { ignoreUnknownKeys = true }.asConverterFactory(
                    GithubDataSource.CONTENT_TYPE.toMediaType()
                )
            )
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
    fun provideRepoDao(database: Lazy<DatabaseManager>): RepoDao {
        return database.get().repoDao()
    }
}
