# Android Clean Architecture

## Table of Contents

[TOC]

## Gradle Setup

See [**build.gradle**](./app/build.gradle) for more details.

### Target

- Android SDK 30 (R)

### Dependencies

- Kotlin + stdlib `1.3.72`
- Android Gradle Plugin `4.0.1` + Databinding enabled
- Hilt `2.28.3-alpha` (DI Library based on Dagger 2)
- Jetpack Hilt `1.0.0-alpha02`
- Multidex `2.0.1` (Avoid the 64K methods limit)
- Kotlin Coroutines `1.3.8`
- Android KTX `1.3.1`
- Room KTX `2.2.5`
- Lifecycle KTX (LiveData + ViewModel) `2.2.0`
- Fragment KTX `1.2.5`
- Navigation KTX + SafeArgs `2.3.0`
- Retrofit `2.9.0` (HTTP Client for APIs)
- Moshi `1.9.3` (JSON Parser)

### Test dependencies

- Kotest `4.1.3` + JUnit 5
- MockK `1.10.0`

### Instrumented test dependencies

- AndroidX Test `1.3.0-rc03`
- Hilt Testing
- JUnit Assertions
- Espresso Core

## Proposed Architecture

### Data

#### Database (Room) / Local Data Source

The cache is stored in [`cleanarchitecture.data.database`](./app/src/main/java/marc/nguyen/cleanarchitecture/data/database).

The DAOs are stored in the `RoomDatabase` abstract class.

```kotlin
@Database(
    entities = [RepoModel::class],
    version = 1
)
abstract class DatabaseManager : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}
```

The DAOs definitions are stored alongside the `DatabaseManager` :

```kotlin
@Dao
interface RepoDao {
    @Query("SELECT * FROM repos WHERE owner_login = :user")
    fun watchReposByUser(user: String): Flow<List<RepoModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<RepoModel>)

    @Query("DELETE FROM repos")
    suspend fun clear()
}
```

Since we use Kotlin Coroutines, we use `suspend` and `Flow` to avoid working on the main thread and listen to changes in the database. This is equivalent to [`Single`](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html) and [`Flowable`](http://reactivex.io/RxJava/javadoc/io/reactivex/Flowable.html) with RxJava.

To use and inject the `DatabaseManager` into repositories, we use Hilt, based on Dagger 2. We create a module in [`cleanarchitecture.core.di`](./app/src/main/java/marc/nguyen/cleanarchitecture/core/di) named `DataModule` which install in the [`ApplicationComponent` (as a singleton)](https://dagger.dev/hilt/components.html#component-lifetimes).

```kotlin
@Module
@InstallIn(ApplicationComponent::class)
object DataModule {
  	/* ... */
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): DatabaseManager {
        return Room.databaseBuilder(
            context,
            DatabaseManager::class.java,
            "clean_architecture.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRepoDao(database: DatabaseManager): RepoDao {
        return database.repoDao()
    }
}
```

#### (Remote) Data Sources

The data sources (responsible for HTTP request and data parsing into models) are stored in [`cleanarchitecture.data.datasources`](./app/src/main/java/marc/nguyen/cleanarchitecture/data/datasources).

Example with Github API with Retrofit :

```kotlin
interface GithubDataSource {
    companion object {
        const val BASE_URL = "https://api.github.com/"
    }

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("users/{user}/repos")
    suspend fun getReposByUser(@Path("user") user: String): List<RepoModel>
}
```

Since we are using Retrofit, we need to build and inject the data sources. We use the same Hilt module `DataModule` stored in [`cleanarchitecture.core.di`](./app/src/main/java/marc/nguyen/cleanarchitecture/core/di) and we provides the data source. (Note : If you want to use the cache of `OkHttpClient`, you can change the `provideHttpClient`)

```kotlin
@Module
@InstallIn(ApplicationComponent::class)
object DataModule {
        @Provides
    @Singleton
    fun provideGithubDataSource(client: OkHttpClient): GithubDataSource {
        return Retrofit.Builder()
            .baseUrl(GithubDataSource.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(GithubDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient() = OkHttpClient()

    // @Provides
  	// @Singleton
    // fun provideCache(ctx: Context): Cache {
    //     return Cache(ctx.cacheDir, CACHE_SIZE)
    // }
    //
    // @Provides
  	// @Singleton
    // fun provideHttpClientWithCache(cache: Cache): OkHttpClient {
    //     return OkHttpClient.Builder()
    //         .cache(cache)
    //         .build()
    // }

    /* ... */
}
```

#### Models

The models are stored in [`cleanarchitecture.data.models`](./app/src/main/java/marc/nguyen/cleanarchitecture/data/models). Models

Example with `RepoModel` which is an entity for Room and can be parsed with Moshi :

```kotlin
@Entity(tableName = "repos")
@JsonClass(generateAdapter = true)
data class RepoModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "full_name") @Json(name = "full_name") val fullName: String,
    @Embedded(prefix = "owner_") val owner: UserModel,
    @ColumnInfo(name = "html_url") @Json(name = "html_url") val htmlUrl: String,
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
```

#### Repositories Implementations

In [`cleanarchitecture.data.repositories`](./app/src/main/java/marc/nguyen/cleanarchitecture/data/repositories), we store the implementations of the [`cleanarchitecture.domain.repositories`](./app/src/main/java/marc/nguyen/cleanarchitecture/domain/repositories). The repositories should decide if we take data from the local cache or the remote database. In addition, repositories must map models to business logic entities, so that these entities contain only data related to the business logic and not to the database (for example, you may want to generate a "ModifiedAt" timestamp for the database, which may not be related to the business logic).

To achieve this, we can either :

- Check connectivity and choose between data sources.
- Or, listen to the local database and refresh the information as new data becomes available.

We will prefer the second method as it decrease the app-loading time, according to the [Google Codelab "Android Kotlin Fundamentals 09.1: Repository"](https://codelabs.developers.google.com/codelabs/kotlin-android-training-repository/index.html?index=..%2F..android-kotlin-fundamentals#4).

```kotlin
@Singleton
class RepoRepositoryImpl @Inject constructor(  // Do not forget to inject the dependencies via Hilt/Dagger 2
    private val remote: GithubDataSource,
    private val local: RepoDao
) : RepoRepository {
    // This method listen to the database
    override fun watchReposByUser(user: String) = local.watchReposByUser(user)
        .map { repos -> repos.map { it.asEntity() } }

    // This method is to refresh the database
    override suspend fun refreshReposByUser(user: String) {
        try {
            val repos = remote.getReposByUser(user)
            local.insertAll(repos)
        } catch (e: SocketTimeoutException) {
            throw NoNetworkException(e)
        } catch (e: UnknownHostException) {
            throw ServerUnreachableException(e)
        } catch (e: HttpException) {
            throw HttpCallFailureException(e)
        } catch (e: Throwable) {
            throw NetworkException(e)
        }
    }
}
```

The interfaces for the repositories are right below !

### Domain

#### Repositories interfaces

The contracts for the repositories are stored in [`cleanarchitecture.domain.repositories`](./app/src/main/java/marc/nguyen/cleanarchitecture/domain/repositories). You may want to add other contracts like the rest of the CRUD : `update`, `delete`, `add`.

```kotlin
interface RepoRepository {
    fun watchAllByUser(user: String): Flow<List<Repo>>

    suspend fun refreshAllByUser(user: String)
}
```

#### Entities

The business logic entities are stored in [`cleanarchitecture.domain.entities`](./app/src/main/java/marc/nguyen/cleanarchitecture/domain/entities). 

```kotlin
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
```

It implements `ModelMappable`, however this is optional. (If it's just a matter of reading data, there is no need for the entities to return to the data layer).

#### Use cases

The use cases interactors are stored in [`cleanarchitecture.domain.usecases`](./app/src/main/java/marc/nguyen/cleanarchitecture/domain/usecases). The use cases orchestrate the flow of data to and from the entities, and direct those entities to use their Critical Business Rules to achieve the goals of the use case.

For example, here it is a matter of fetching data either from the cache or from the network, or fetching failures either from the cache or from the network.

So, you may want to write the tests first before writing your use case :

```kotlin
class WatchReposByUserTest : WordSpec({
    val repoRepository = mockk<RepoRepository>()
    val watchReposByUser: FlowUsecase<String, List<Repo>> = WatchReposByUser(repoRepository)

    beforeTest {
        clearAllMocks()
    }

    "invoke" should {
        "emit data with cache data and network success" {
            // Arrange
            val repos = listOf(TestUtil.createRepo(0))
            coEvery { repoRepository.refreshAllByUser(any()) } returns Unit
            every { repoRepository.watchAllByUser(any()) } returns flowOf(repos)

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isSuccess shouldBe true
            result.getOrNull() shouldBe repos
        }

        "emit data with cache data and network failure" {
            // Arrange
            val repos = listOf(TestUtil.createRepo(0))
            coEvery { repoRepository.refreshAllByUser(any()) } throws NetworkException(Exception())
            every { repoRepository.watchAllByUser(any()) } returns flowOf(repos)

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isSuccess shouldBe true
            result.getOrNull() shouldBe repos
        }

        "emit failure on network failure" {
            // Arrange
            coEvery { repoRepository.refreshAllByUser(any()) } throws NetworkException(Exception())
            every { repoRepository.watchAllByUser(any()) } returns flowOf(emptyList())  // No cache data

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeTypeOf<NetworkException>()
        }

        "emit failure on cache failure" {
            // Arrange
            coEvery { repoRepository.refreshAllByUser(any()) } returns Unit  // Anything
            every { repoRepository.watchAllByUser(any()) } returns flow {
                throw Exception()
            }

            // Act
            val result = watchReposByUser("user").first()

            // Assert
            result.isFailure shouldBe true
            result.exceptionOrNull().shouldBeTypeOf<CacheException>()
        }
    }
})
```

Then, you should implement the use case :

```kotlin
@Singleton
class WatchReposByUser @Inject constructor(private val repoRepository: RepoRepository) :
    FlowUsecase<String, List<Repo>> {
    override operator fun invoke(params: String): Flow<Result<List<Repo>>> = flow {
        val networkResult = kotlin.runCatching {
            repoRepository.refreshAllByUser(params)
        }

        emitAll(
            repoRepository.watchAllByUser(params).map {  // it: List<Repo>
                if (it.isNotEmpty()) {
                    Result.success(it)
                } else {
                    Result.failure(
                        networkResult.exceptionOrNull()
                            ?: CacheException(Exception("No data available"))
                    )
                }
            }.catch {
                emit(Result.failure(CacheException(it)))
            }
        )
    }
}
```

Note : This implement a `FlowUsecase` which is :

```kotlin
interface FlowUsecase<in Params, out Type> {
    operator fun invoke(params: Params): Flow<Result<Type>>
}
```

If this is a simple call, like a simple `UpdateRepo`, you may prefer to implement `Usecase` instead :

```kotlin
interface Usecase<in Params, out Type> {
    suspend operator fun invoke(params: Params): Result<Type>

    object None  // Empty Params
}
```

### Presentation

The presentation layer is following the Android Kotlin Fundamentals Course from the Google Codelab.

#### UI

This is only for our use case, you may want to change everything. We are using a `RecyclerView` and a `Fragment`. The `Fragment` is bound to a `ViewModel`.

##### Fragment

```kotlin
@AndroidEntryPoint
class GithubFragment : Fragment() {
    private val args by navArgs<GithubFragmentArgs>()

    @Inject
    lateinit var githubViewModelAssistedFactory: GithubViewModel.AssistedFactory

    private val viewModel by viewModels<GithubViewModel> {
        GithubViewModel.provideFactory(githubViewModelAssistedFactory, args.user)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Bind
        val binding: GithubFragmentBinding = GithubFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.repoList.adapter = GithubAdapter(GithubAdapter.OnClickListener { // it: Repo
            onClick(it)
        })

        return binding.root
    }

    private fun onClick(repo: Repo) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
        startActivity(intent)
    }
}
```

##### RecyclerViewAdapter

```kotlin
class GithubAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Repo, GithubAdapter.ViewHolder>(DiffCallback) {
    // Automatically refresh the RecyclerView
    companion object DiffCallback : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(
            oldItem: Repo,
            newItem: Repo
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Repo,
            newItem: Repo
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ViewHolder(
        private var binding: GithubRepoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        // Bind a repo to a ViewHolder
        fun bind(repo: Repo) {
            binding.repo = repo
            binding.executePendingBindings()
        }
    }

    // ClickListener on each item
    class OnClickListener(val clickListener: (repo: Repo) -> Unit) {
        fun onClick(repo: Repo) = clickListener(repo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            GithubRepoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Bind each Repo to a ViewHolder and a ClickListener
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(repo)
        }
        holder.bind(repo)
    }
}
```

You should also check the [Binding Adapters](./app/src/main/java/marc/nguyen/cleanarchitecture/presentation/util/BindingAdapters.kt) and the [layouts](./app/src/main/res/layout) .

#### ViewModels

For the ViewModels, we use `AssistedInject` to partially inject the dependencies, because we need the `user` in the constructor of the viewmodel.

We convert the `Flow` into `LiveData`. We also use a `MutableLiveData` for managing the `Loading` and `Error` state.

```kotlin
class GithubViewModel @AssistedInject constructor(
    @Assisted private val user: String,
    watchReposByUser: WatchReposByUser
) : ViewModel() {
    // RefreshData's state
    private var _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    val repos = watchReposByUser(user)
        .map { result ->
            result.fold(
                {
                    _state.value = State.Loaded(it)
                    it
                },
                {
                    _state.value = State.Error(it)
                    emptyList()
                }
            )
        }.asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(user: String): GithubViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            user: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(user) as T
            }
        }
    }
}
```

If we use assisted injection, we must declare a `AssistedModule` :

```kotlin
@InstallIn(FragmentComponent::class)
@AssistedModule
@Module(includes = [AssistedInject_AssistedInjectModule::class])
interface AssistedInjectModule {}
```

Since we only use one ViewModel per Fragment, we can use `@InstallIn(FragmentComponent::class)`.