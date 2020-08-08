package marc.nguyen.cleanarchitecture.data.repositories

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import marc.nguyen.cleanarchitecture.core.exception.HttpCallFailureException
import marc.nguyen.cleanarchitecture.core.exception.NetworkException
import marc.nguyen.cleanarchitecture.core.exception.NoNetworkException
import marc.nguyen.cleanarchitecture.core.exception.ServerUnreachableException
import marc.nguyen.cleanarchitecture.data.database.RepoDao
import marc.nguyen.cleanarchitecture.data.datasources.GithubDataSource
import marc.nguyen.cleanarchitecture.utils.TestUtil
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RepoRepositoryImplTest : WordSpec({
    val remote = mockk<GithubDataSource>()
    val local = mockk<RepoDao>()
    val repoRepository = RepoRepositoryImpl(remote, local)

    beforeTest {
        clearAllMocks()
    }

    "watchReposByUser" should {
        "get repos" {
            // Arrange
            val repos = listOf(
                TestUtil.createRepoModel(0, "user"),
                TestUtil.createRepoModel(1, "user")
            )
            every { local.watchReposByUser("user") } returns flowOf(repos)

            // Act
            val result = repoRepository.watchReposByUser("user").first()

            // Assert
            result shouldBe repos.map { it.asEntity() }
        }
    }

    "refreshReposByUser" should {
        "get repos and fill the cache" {
            // Arrange
            val repos = listOf(
                TestUtil.createRepoModel(0, "user"),
                TestUtil.createRepoModel(1, "user")
            )
            coEvery { remote.getReposByUser("user") } returns repos
            coEvery { local.insertAll(any()) } returns Unit

            // Act
            repoRepository.refreshReposByUser("user")

            // Assert
            coVerify { local.insertAll(repos) }
        }

        "throw NoNetworkException if no Wifi" {
            // Arrange
            coEvery { remote.getReposByUser("user") } throws SocketTimeoutException()

            // Act and Assert
            shouldThrow<NoNetworkException> {
                repoRepository.refreshReposByUser("user")
            }
        }


        "throw ServerUnreachableException if ServerUnreachable" {
            // Arrange
            coEvery { remote.getReposByUser("user") } throws UnknownHostException()

            // Act and Assert
            shouldThrow<ServerUnreachableException> {
                repoRepository.refreshReposByUser("user")
            }
        }

        "throw HttpCallFailureException if HTTP Error" {
            // Arrange
            coEvery { remote.getReposByUser("user") } throws HttpException(
                Response.error<Unit>(
                    404,
                    ResponseBody.create(null, "Not Found")
                )
            )

            // Act and Assert
            shouldThrow<HttpCallFailureException> {
                repoRepository.refreshReposByUser("user")
            }
        }

        "throw NetworkException if remote throw" {
            // Arrange
            coEvery { remote.getReposByUser("user") } throws Exception()

            // Act and Assert
            shouldThrow<NetworkException> {
                repoRepository.refreshReposByUser("user")
            }
        }
    }
})