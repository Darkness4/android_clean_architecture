package marc.nguyen.cleanarchitecture.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser

class GithubViewModel @AssistedInject constructor(
    @Assisted private val user: String,
    private val refreshReposByUser: RefreshReposByUser,
    watchReposByUser: WatchReposByUser
) : ViewModel() {
    private val _networkStatus = MutableLiveData<Result<Unit>>()
    val networkStatus: LiveData<Result<Unit>>
        get() = _networkStatus

    val state: LiveData<Result<List<Repo>>> = watchReposByUser(user)
        .asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)

    init {
        refreshRepos()
    }

    fun refreshRepos() {
        viewModelScope.launch {
            _networkStatus.value = refreshReposByUser(user)
        }
    }

    fun refreshReposDone() {
        _networkStatus.value = null
    }

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