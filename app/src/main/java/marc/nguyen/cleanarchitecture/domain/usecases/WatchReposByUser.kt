package marc.nguyen.cleanarchitecture.domain.usecases

import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import marc.nguyen.cleanarchitecture.core.exception.CacheException
import marc.nguyen.cleanarchitecture.core.result.Result
import marc.nguyen.cleanarchitecture.core.usecase.FlowUseCase
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchReposByUser @Inject constructor(
    private val repoRepository: Lazy<RepoRepository>
) :
    FlowUseCase<String, List<Repo>> {
    override operator fun invoke(params: String): Flow<Result<List<Repo>>> =
        repoRepository.get().watchAllByUser(params).map {
            if (it.isNotEmpty()) {
                Result.Success(it)
            } else {
                Result.Failure(CacheException("No data available"))
            }
        }.catch {
            emit(Result.Failure(CacheException(it.message)))
        }.flowOn(Dispatchers.IO)
}
