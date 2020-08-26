package marc.nguyen.cleanarchitecture.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser

class GithubViewModel @AssistedInject constructor(
    @Assisted private val user: String,
    private val refreshReposByUser: RefreshReposByUser,
    watchReposByUser: WatchReposByUser
) : ViewModel() {
    private val _networkStatus = MutableLiveData<Either<Throwable, Unit>>()
    val networkStatus: LiveData<Either<Throwable, Unit>>
        get() = _networkStatus

    val state = watchReposByUser(user)
        .asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)

    private val _isManuallyRefreshing = MutableLiveData(false)
    val isManuallyRefreshing
        get() = _isManuallyRefreshing

    init {
        refreshRepos()
    }

    private fun refreshRepos() {
        viewModelScope.launch {
            _networkStatus.value = refreshReposByUser(user)
        }
    }

    fun manualRefresh() {
        _isManuallyRefreshing.value = true
        refreshRepos()
    }

    fun manualRefreshDone() {
        _isManuallyRefreshing.value = false
    }

    @AssistedInject.Factory
    fun interface AssistedFactory {
        fun create(user: String): GithubViewModel
    }

    class Provider(
        private val assistedFactory: AssistedFactory,
        private val user: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return assistedFactory.create(user) as T
        }
    }
}
