package marc.nguyen.cleanarchitecture.core.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser
import marc.nguyen.cleanarchitecture.presentation.viewmodels.GithubViewModel

@Module
@InstallIn(FragmentComponent::class)
object PresentationModule {
    @Provides
    fun provideGithubViewModelInteractors(
        watchReposByUser: Lazy<WatchReposByUser>,
        refreshReposByUser: Lazy<RefreshReposByUser>
    ) =
        GithubViewModel.Interactors(refreshReposByUser, watchReposByUser)
}
