package marc.nguyen.cleanarchitecture.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import marc.nguyen.cleanarchitecture.core.exception.CacheException
import marc.nguyen.cleanarchitecture.core.usecase.FlowUsecase
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchReposByUser @Inject constructor(
    private val repoRepository: RepoRepository
) :
    FlowUsecase<String, List<Repo>> {
    override operator fun invoke(params: String): Flow<Result<List<Repo>>> =
        repoRepository.watchAllByUser(params).map {
            if (it.isNotEmpty()) {
                Result.success(it)
            } else {
                Result.failure(CacheException(Exception("No data available")))
            }
        }.catch {
            emit(Result.failure(CacheException(it)))
        }
}