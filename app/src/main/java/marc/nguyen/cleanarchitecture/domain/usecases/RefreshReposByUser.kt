package marc.nguyen.cleanarchitecture.domain.usecases

import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import marc.nguyen.cleanarchitecture.core.result.Result
import marc.nguyen.cleanarchitecture.core.usecase.UseCase
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshReposByUser @Inject constructor(private val repoRepository: Lazy<RepoRepository>) :
    UseCase<String, Unit> {
    override suspend operator fun invoke(params: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(repoRepository.get().refreshAllByUser(params))
            } catch (e: Throwable) {
                Result.Failure(e)
            }
        }
}
