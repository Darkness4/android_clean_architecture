package marc.nguyen.cleanarchitecture.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import marc.nguyen.cleanarchitecture.core.mappers.DomainMappable
import marc.nguyen.cleanarchitecture.domain.entities.Repo

@Entity(tableName = "repos")
@JsonClass(generateAdapter = true)
data class RepoModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "full_name") @Json(name = "full_name") val fullName: String,
    @Embedded(prefix = "owner_") val owner: UserModel
) : DomainMappable<Repo> {
    override fun asEntity(): Repo =
        Repo(
            id = this.id,
            name = this.name,
            fullName = this.fullName,
            owner = this.owner.asEntity()
        )
}