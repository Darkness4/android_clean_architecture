package marc.nguyen.cleanarchitecture.domain.usecases

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import marc.nguyen.cleanarchitecture.core.exception.CacheException
import marc.nguyen.cleanarchitecture.core.usecase.FlowUseCase
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchReposByUser @Inject constructor(
    private val repoRepository: RepoRepository
) :
    FlowUseCase<String, List<Repo>> {
    override operator fun invoke(params: String): Flow<Either<Throwable, List<Repo>>> =
        repoRepository.watchAllByUser(params).map {
            if (it.isNotEmpty()) {
                Right(it)
            } else {
                Left(CacheException("No data available"))
            }
        }.catch {
            emit(Left(CacheException(it.message)))
        }
}
