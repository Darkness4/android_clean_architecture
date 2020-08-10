package marc.nguyen.cleanarchitecture.data.repositories

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
    private val remote: GithubDataSource,
    private val local: RepoDao
) : RepoRepository {
    override fun watchAllByUser(user: String) = local.watchReposByUser(user)
        .map { repos -> repos.map { it.asEntity() } }

    override suspend fun refreshAllByUser(user: String) {
        try {
            val repos = remote.getReposByUser(user)
            local.insertAll(repos)
        } catch (e: SocketTimeoutException) {
            throw NoNetworkException(e)
        } catch (e: UnknownHostException) {
            throw ServerUnreachableException(e)
        } catch (e: HttpException) {
            throw HttpCallFailureException(e)
        } catch (e: Throwable) {
            throw NetworkException(e)
        }
    }
}