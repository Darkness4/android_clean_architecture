package marc.nguyen.cleanarchitecture.domain.repositories

import kotlinx.coroutines.flow.Flow
import marc.nguyen.cleanarchitecture.domain.entities.Repo

interface RepoRepository {
    fun watchReposByUser(user: String): Flow<List<Repo>>

    suspend fun refreshReposByUser(user: String)
}
