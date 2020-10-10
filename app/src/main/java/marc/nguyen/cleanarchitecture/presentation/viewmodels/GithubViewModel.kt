package marc.nguyen.cleanarchitecture.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import marc.nguyen.cleanarchitecture.core.result.Result
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser
import javax.inject.Inject

class GithubViewModel constructor(
    private val user: String,
    private val interactors: Interactors
) : ViewModel() {
    class Interactors @Inject constructor(
        val refreshReposByUser: Lazy<RefreshReposByUser>,
        val watchReposByUser: Lazy<WatchReposByUser>
    )

    private val _networkStatus = MutableLiveData<Result<Unit>>()
    val networkStatus: LiveData<Result<Unit>>
        get() = _networkStatus

    val state =
        interactors.watchReposByUser.get()(user).asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)

    private val _isManuallyRefreshing = MutableLiveData(false)
    val isManuallyRefreshing
        get() = _isManuallyRefreshing

    init {
        refreshRepos()
    }

    private fun refreshRepos() {
        viewModelScope.launch {
            _networkStatus.value = interactors.refreshReposByUser.get()(user)
        }
    }

    fun manualRefresh() {
        _isManuallyRefreshing.value = true
        refreshRepos()
    }

    fun manualRefreshDone() {
        _isManuallyRefreshing.value = false
    }

    class Factory(
        private val interactors: Interactors,
        private val user: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return GithubViewModel(user, interactors) as T
        }
    }
}
