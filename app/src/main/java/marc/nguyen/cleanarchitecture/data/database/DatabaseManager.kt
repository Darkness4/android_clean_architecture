package marc.nguyen.cleanarchitecture.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import marc.nguyen.cleanarchitecture.data.models.RepoModel

@Database(
    entities = [RepoModel::class],
    version = 1
)
abstract class DatabaseManager : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}