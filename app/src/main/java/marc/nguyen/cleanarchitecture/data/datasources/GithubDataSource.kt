package marc.nguyen.cleanarchitecture.data.datasources

import marc.nguyen.cleanarchitecture.data.models.RepoModel
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GithubDataSource {
    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val CONTENT_TYPE = "application/json; charset=utf-8"
    }

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("users/{user}/repos")
    suspend fun getReposByUser(@Path("user") user: String): List<RepoModel>
}
