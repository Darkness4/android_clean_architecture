package marc.nguyen.cleanarchitecture.domain.usecases

import dagger.Lazy
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import marc.nguyen.cleanarchitecture.core.exception.CacheException
import marc.nguyen.cleanarchitecture.core.usecase.FlowUseCase
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository
import marc.nguyen.cleanarchitecture.utils.TestUtil

class WatchReposByUserTest : WordSpec({
    val repoRepositoryLazy = mockk<Lazy<RepoRepository>>()
    val repoRepository = mockk<RepoRepository>()
    val watchReposByUser: FlowUseCase<String, List<Repo>> = WatchReposByUser(repoRepositoryLazy)

    beforeTest {
        clearAllMocks()
        every { repoRepositoryLazy.get() } returns repoRepository
    }

    "invoke" should {
        "emit data with cache data" {
            // Arrange
            val repos = listOf(TestUtil.createRepo(0))
            every { repoRepository.watchAllByUser(any()) } returns flowOf(repos)

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isSuccess shouldBe true
            result.valueOrNull() shouldBe repos
        }

        "emit failure on empty cache" {
            // Arrange
            every { repoRepository.watchAllByUser(any()) } returns flowOf(emptyList())

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeTypeOf<CacheException>()
        }

        "emit failure on cache failure" {
            // Arrange
            every { repoRepository.watchAllByUser(any()) } returns flow {
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
