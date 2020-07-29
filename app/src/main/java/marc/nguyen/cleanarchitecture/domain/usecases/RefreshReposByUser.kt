package marc.nguyen.cleanarchitecture.domain.usecases

import marc.nguyen.cleanarchitecture.core.usecase.Usecase
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshReposByUser @Inject constructor(private val repoRepository: RepoRepository) :
    Usecase<String, Unit> {
    override suspend operator fun invoke(params: String) =
        repoRepository.refreshReposByUser(params)
}