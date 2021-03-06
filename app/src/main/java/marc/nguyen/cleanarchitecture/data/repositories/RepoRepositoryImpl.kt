package marc.nguyen.cleanarchitecture.data.repositories

import dagger.Lazy
import kotlinx.coroutines.flow.map
import marc.nguyen.cleanarchitecture.core.exception.HttpCallFailureException
import marc.nguyen.cleanarchitecture.core.exception.NetworkException
import marc.nguyen.cleanarchitecture.core.exception.NoNetworkException
import marc.nguyen.cleanarchitecture.core.exception.ServerUnreachableException
import marc.nguyen.cleanarchitecture.data.database.RepoDao
import marc.nguyen.cleanarchitecture.data.datasources.GithubDataSource
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepositoryImpl @Inject constructor(
    private val remote: Lazy<GithubDataSource>,
    private val local: Lazy<RepoDao>
) : RepoRepository {
    override fun watchAllByUser(user: String) = local.get().watchReposByUser(user)
        .map { repos -> repos.map { it.asEntity() } }

    override suspend fun refreshAllByUser(user: String) {
        try {
            val repos = remote.get().getReposByUser(user)
            local.get().insertAll(repos)
        } catch (e: SocketTimeoutException) {
            throw NoNetworkException(e.message)
        } catch (e: UnknownHostException) {
            throw ServerUnreachableException(e.message)
        } catch (e: HttpException) {
            throw HttpCallFailureException(e.message)
        } catch (e: Throwable) {
            throw NetworkException(e.message)
        }
    }
}
