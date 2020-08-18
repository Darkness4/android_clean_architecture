package marc.nguyen.cleanarchitecture.data.models

import com.squareup.moshi.JsonClass
import marc.nguyen.cleanarchitecture.core.mappers.DomainMappable
import marc.nguyen.cleanarchitecture.domain.entities.User

@JsonClass(generateAdapter = true)
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
