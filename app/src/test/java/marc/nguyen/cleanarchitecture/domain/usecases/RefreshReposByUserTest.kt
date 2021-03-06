package marc.nguyen.cleanarchitecture.domain.usecases

import dagger.Lazy
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import marc.nguyen.cleanarchitecture.core.usecase.UseCase
import marc.nguyen.cleanarchitecture.domain.repositories.RepoRepository

class RefreshReposByUserTest : WordSpec({
    val repoRepositoryLazy = mockk<Lazy<RepoRepository>>()
    val repoRepository = mockk<RepoRepository>()
    val refreshAllByUser: UseCase<String, Unit> = RefreshReposByUser(repoRepositoryLazy)

    beforeTest {
        clearAllMocks()
        every { repoRepositoryLazy.get() } returns repoRepository
    }

    "invoke" should {
        "return a Unit" {
            // Arrange
            coEvery { repoRepository.refreshAllByUser(any()) } returns Unit

            // Act
            val result = refreshAllByUser("user")

            // Assert
            result.isSuccess shouldBe true
            result.valueOrNull() shouldBe Unit
        }

        "return failure on throw" {
            // Arrange
            coEvery { repoRepository.refreshAllByUser(any()) } throws Exception()

            // Act
            val result = refreshAllByUser("user")

            // Assert
            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeTypeOf<Exception>()
        }
    }
})
