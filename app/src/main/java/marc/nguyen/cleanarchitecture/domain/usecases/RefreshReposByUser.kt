package marc.nguyen.cleanarchitecture.domain.usecases

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import marc.nguyen.cleanarchitecture.core.usecase.UseCase
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshReposByUser @Inject constructor(private val repoRepository: RepoRepository) :
    UseCase<String, Unit> {
    override suspend operator fun invoke(params: String): Either<Throwable, Unit> =
        try {
            Right(repoRepository.refreshAllByUser(params))
        } catch (e: Throwable) {
            Left(e)
        }
}
