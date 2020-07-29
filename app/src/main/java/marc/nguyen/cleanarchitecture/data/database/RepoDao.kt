package marc.nguyen.cleanarchitecture.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import marc.nguyen.cleanarchitecture.data.models.RepoModel

@Dao
interface RepoDao {
    @Query("SELECT * FROM repos WHERE owner_login = :user")
    fun watchReposByUser(user: String): Flow<List<RepoModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<RepoModel>)

    @Query("DELETE FROM repos")
    suspend fun clear()
}