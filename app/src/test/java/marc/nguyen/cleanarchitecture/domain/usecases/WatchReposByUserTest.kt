package marc.nguyen.cleanarchitecture.domain.usecases

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import marc.nguyen.cleanarchitecture.core.exception.CacheException
import marc.nguyen.cleanarchitecture.core.exception.NetworkException
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import marc.nguyen.cleanarchitecture.utils.TestUtil

class WatchReposByUserTest : WordSpec({
    val repoRepository = mockk<RepoRepository>()
    val watchReposByUser = WatchReposByUser(repoRepository)

    beforeTest {
        clearAllMocks()
    }

    "invoke" should {
        "emit data with cache data and network success" {
            // Arrange
            val repos = listOf(TestUtil.createRepo(0))
            coEvery { repoRepository.refreshReposByUser(any()) } returns Unit
            every { repoRepository.watchReposByUser(any()) } returns flowOf(repos)

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isSuccess shouldBe true
            result.getOrNull() shouldBe repos
        }

        "emit data with cache data and network failure" {
            // Arrange
            val repos = listOf(TestUtil.createRepo(0))
            coEvery { repoRepository.refreshReposByUser(any()) } throws NetworkException(Exception())
            every { repoRepository.watchReposByUser(any()) } returns flowOf(repos)

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isSuccess shouldBe true
            result.getOrNull() shouldBe repos
        }

        "emit failure on network failure" {
            // Arrange
            coEvery { repoRepository.refreshReposByUser(any()) } throws NetworkException(Exception())
            every { repoRepository.watchReposByUser(any()) } returns flowOf(emptyList())

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeTypeOf<NetworkException>()
        }

        "emit failure on cache failure" {
            // Arrange
            coEvery { repoRepository.refreshReposByUser(any()) } returns Unit
            every { repoRepository.watchReposByUser(any()) } returns flow {
                throw Exception()
            }

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeTypeOf<CacheException>()
        }
    }
})