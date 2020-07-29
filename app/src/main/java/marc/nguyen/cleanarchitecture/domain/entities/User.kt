package marc.nguyen.cleanarchitecture.domain.entities

import marc.nguyen.cleanarchitecture.core.mappers.ModelMappable
import marc.nguyen.cleanarchitecture.data.models.UserModel

data class User(
    val id: Int,
    val login: String
) : ModelMappable<UserModel> {
    override fun asModel(): UserModel =
        UserModel(
            id = this.id,
            login = this.login
        )
}