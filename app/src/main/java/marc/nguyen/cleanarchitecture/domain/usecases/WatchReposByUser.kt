package marc.nguyen.cleanarchitecture.domain.usecases

import kotlinx.coroutines.flow.Flow
import marc.nguyen.cleanarchitecture.core.usecase.FlowUsecase
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchReposByUser @Inject constructor(private val repoRepository: RepoRepository) :
    FlowUsecase<String, List<Repo>> {
    override operator fun invoke(params: String): Flow<List<Repo>> =
        repoRepository.watchReposByUser(params)
}