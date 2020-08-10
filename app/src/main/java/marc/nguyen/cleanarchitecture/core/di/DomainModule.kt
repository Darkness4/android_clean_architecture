package marc.nguyen.cleanarchitecture.core.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import marc.nguyen.cleanarchitecture.data.repositories.RepoRepositoryImpl
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DomainModule {
    @Singleton
    @Provides
    fun provideWatchReposByUser(repoRepository: RepoRepository) = WatchReposByUser(repoRepository)

    @Singleton
    @Provides
    fun provideRefreshReposByUser(repoRepository: RepoRepository) =
        RefreshReposByUser(repoRepository)
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class DomainBindModule {
    @Binds
    abstract fun bindRepoRepository(
        repoRepositoryImpl: RepoRepositoryImpl
    ): RepoRepository
}