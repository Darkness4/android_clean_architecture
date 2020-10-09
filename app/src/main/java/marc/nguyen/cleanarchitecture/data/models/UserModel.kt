package marc.nguyen.cleanarchitecture.data.models

import kotlinx.serialization.Serializable
import marc.nguyen.cleanarchitecture.core.mappers.DomainMappable
import marc.nguyen.cleanarchitecture.domain.entities.User

@Serializable
data class UserModel(
    val id: Int,
    val login: String
) : DomainMappable<User> {
    override fun asEntity(): User =
        User(
            id = this.id,
            login = this.login
        )
}
