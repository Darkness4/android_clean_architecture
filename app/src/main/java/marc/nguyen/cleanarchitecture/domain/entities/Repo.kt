package marc.nguyen.cleanarchitecture.domain.entities

import marc.nguyen.cleanarchitecture.core.mappers.ModelMappable
import marc.nguyen.cleanarchitecture.data.models.RepoModel

data class Repo(
    val id: Int,
    val name: String,
    val fullName: String,
    val owner: User,
    val htmlUrl: String,
    val description: String?
) : ModelMappable<RepoModel> {
    override fun asModel() =
        RepoModel(
            id = this.id,
            name = this.name,
            fullName = this.fullName,
            owner = this.owner.asModel(),
            htmlUrl = this.htmlUrl,
            description = description
        )
}
