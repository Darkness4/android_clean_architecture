package marc.nguyen.cleanarchitecture.utils

import marc.nguyen.cleanarchitecture.data.models.RepoModel
import marc.nguyen.cleanarchitecture.data.models.UserModel
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.domain.entities.User

object TestUtil {
    fun createRepoModel(id: Int, user: String = "user", name: String = "name") = RepoModel(
        id = id,
        name = name,
        owner = UserModel(
            id = 0,
            login = user
        ),
        description = "description",
        fullName = "$user/$name",
        htmlUrl = "https://localhost/"
    )

    fun createRepo(id: Int, user: String = "user", name: String = "name") = Repo(
        id = id,
        name = name,
        owner = User(
            id = 0,
            login = user
        ),
        description = "description",
        fullName = "$user/$name",
        htmlUrl = "https://localhost/"
    )
}