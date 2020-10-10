package marc.nguyen.cleanarchitecture.core.usecase

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import marc.nguyen.cleanarchitecture.core.result.Result

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This abstraction represents an execution unit for different use cases (this means than any use
 * case in the application should implement this contract).
 */
interface UseCase<in Params, out Type> {
    suspend operator fun invoke(params: Params): Result<Type>
}

interface FlowUseCase<in Params, out Type> {
    operator fun invoke(params: Params): Flow<Result<Type>>
}

interface LiveDataUseCase<in Params, Type> {
    operator fun invoke(params: Params): LiveData<Result<Type>>
}
