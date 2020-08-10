package marc.nguyen.cleanarchitecture.core.usecase

import kotlinx.coroutines.flow.Flow

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This abstraction represents an execution unit for different use cases (this means than any use
 * case in the application should implement this contract).
 */
interface Usecase<in Params, out Type> {
    suspend operator fun invoke(params: Params): Result<Type>

    object None
}

interface FlowUsecase<in Params, out Type> {
    operator fun invoke(params: Params): Flow<Result<Type>>
}