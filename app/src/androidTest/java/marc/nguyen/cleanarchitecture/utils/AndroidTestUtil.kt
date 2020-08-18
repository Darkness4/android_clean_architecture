package marc.nguyen.cleanarchitecture.utils

import marc.nguyen.cleanarchitecture.data.models.RepoModel
import marc.nguyen.cleanarchitecture.data.models.UserModel

object AndroidTestUtil {
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
}
