package marc.nguyen.cleanarchitecture.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser
import javax.inject.Inject

class GithubViewModel constructor(
    private val user: String,
    private val refreshReposByUser: Lazy<RefreshReposByUser>,
    watchReposByUser: Lazy<WatchReposByUser>
) : ViewModel() {
    private val _networkStatus = MutableLiveData<Either<Throwable, Unit>>()
    val networkStatus: LiveData<Either<Throwable, Unit>>
        get() = _networkStatus

    val state = watchReposByUser.get()(user)
        .asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)

    private val _isManuallyRefreshing = MutableLiveData(false)
    val isManuallyRefreshing
        get() = _isManuallyRefreshing

    init {
        refreshRepos()
    }

    private fun refreshRepos() {
        viewModelScope.launch {
            _networkStatus.value = refreshReposByUser.get()(user)
        }
    }

    fun manualRefresh() {
        _isManuallyRefreshing.value = true
        refreshRepos()
    }

    fun manualRefreshDone() {
        _isManuallyRefreshing.value = false
    }

    class Factory @Inject constructor(
        private val refreshReposByUser: Lazy<RefreshReposByUser>,
        private val watchReposByUser: Lazy<WatchReposByUser>
    ) {
        fun create(user: String): GithubViewModel {
            return GithubViewModel(
                user,
                refreshReposByUser,
                watchReposByUser
            )
        }
    }

    class Provider(
        private val factory: Factory,
        private val user: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return factory.create(user) as T
        }
    }
}
