package marc.nguyen.cleanarchitecture.data.models

import kotlinx.serialization.Serializable
import marc.nguyen.cleanarchitecture.core.mappers.EntityMappable
import marc.nguyen.cleanarchitecture.domain.entities.User

@Serializable
data class UserModel(
    val id: Int,
    val login: String
) : EntityMappable<User> {
    override fun asEntity(): User =
        User(
            id = this.id,
            login = this.login
        )
}
