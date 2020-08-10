package marc.nguyen.cleanarchitecture.domain.repositories

import kotlinx.coroutines.flow.Flow
import marc.nguyen.cleanarchitecture.domain.entities.Repo

interface RepoRepository {
    fun watchAllByUser(user: String): Flow<List<Repo>>

    suspend fun refreshAllByUser(user: String)
}
