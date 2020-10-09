package marc.nguyen.cleanarchitecture.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import marc.nguyen.cleanarchitecture.core.mappers.DomainMappable
import marc.nguyen.cleanarchitecture.domain.entities.Repo

@Entity(tableName = "repos")
@Serializable
data class RepoModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "full_name") @SerialName("full_name") val fullName: String,
    @Embedded(prefix = "owner_") val owner: UserModel,
    @ColumnInfo(name = "html_url") @SerialName("html_url") val htmlUrl: String,
    val description: String?
) : DomainMappable<Repo> {
    override fun asEntity(): Repo =
        Repo(
            id = this.id,
            name = this.name,
            fullName = this.fullName,
            owner = this.owner.asEntity(),
            htmlUrl = this.htmlUrl,
            description = this.description
        )
}
