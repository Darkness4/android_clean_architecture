package marc.nguyen.cleanarchitecture.core.usecase

import arrow.core.Either
import kotlinx.coroutines.flow.Flow

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This abstraction represents an execution unit for different use cases (this means than any use
 * case in the application should implement this contract).
 */
interface UseCase<in Params, out Type> {
    suspend operator fun invoke(params: Params): Either<Throwable, Type>
}

interface FlowUseCase<in Params, out Type> {
    operator fun invoke(params: Params): Flow<Either<Throwable, Type>>
}
