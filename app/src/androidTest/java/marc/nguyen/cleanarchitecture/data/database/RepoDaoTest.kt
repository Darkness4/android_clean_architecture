package marc.nguyen.cleanarchitecture.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import marc.nguyen.cleanarchitecture.utils.AndroidTestUtil
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RepoDaoTest {
    private lateinit var repoDao: RepoDao
    private lateinit var db: DatabaseManager

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            DatabaseManager::class.java
        ).build()
        repoDao = db.repoDao()
    }

    @Test
    fun watchReposByUserAndInsertAll() = runBlocking {
        // Arrange
        val reposOfUser = listOf(
            AndroidTestUtil.createRepoModel(0, user = "user"),
            AndroidTestUtil.createRepoModel(1, user = "user")
        )
        val reposOfUser2 = listOf(
            AndroidTestUtil.createRepoModel(2, user = "user2"),
            AndroidTestUtil.createRepoModel(3, user = "user2")
        )

        // Act
        val resultDeferred = async {
            repoDao.watchReposByUser("user")
                .first()
        }
        repoDao.insertAll(reposOfUser + reposOfUser2)

        // Assert
        val result = resultDeferred.await()
        Assert.assertEquals(reposOfUser, result)
    }

    @Test
    fun watchReposByUserAndClear_ExistingDb() = runBlocking {
        // Arrange
        val reposOfUser = listOf(
            AndroidTestUtil.createRepoModel(0, user = "user"),
            AndroidTestUtil.createRepoModel(1, user = "user")
        )

        // Act
        val resultDeferred = async {
            repoDao.watchReposByUser("user")
                .take(2)
                .toList()
        }
        repoDao.insertAll(reposOfUser)
        repoDao.clear()

        // Assert
        val result = resultDeferred.await()
        Assert.assertEquals(
            listOf(
                reposOfUser,
                emptyList()
            ),
            result
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}
