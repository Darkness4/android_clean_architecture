package marc.nguyen.cleanarchitecture.core.di

import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import marc.nguyen.cleanarchitecture.data.repositories.RepoRepositoryImpl
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser
import javax.inject.Singleton

@Module(includes = [DomainModule.Provider::class])
@InstallIn(ApplicationComponent::class)
interface DomainModule {
    @Binds
    fun bindRepoRepository(repoRepositoryImpl: RepoRepositoryImpl): RepoRepository

    @Module
    @InstallIn(ApplicationComponent::class)
    object Provider {
        @Singleton
        @Provides
        fun provideWatchReposByUser(repoRepository: Lazy<RepoRepository>) = WatchReposByUser(repoRepository)

        @Singleton
        @Provides
        fun provideRefreshReposByUser(repoRepository: Lazy<RepoRepository>) =
            RefreshReposByUser(repoRepository)
    }
}
