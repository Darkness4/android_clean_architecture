package marc.nguyen.cleanarchitecture.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import marc.nguyen.cleanarchitecture.core.exception.DataException
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser

class GithubViewModel(
    user: String,
    private val refreshReposByUser: RefreshReposByUser,
    watchReposByUser: WatchReposByUser
) : ViewModel() {
    private var _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    val repos = watchReposByUser(user)
        .asLiveData(Dispatchers.Main + viewModelScope.coroutineContext)

    init {
        refreshDataFromRepository(user)
    }

    private fun refreshDataFromRepository(user: String) {
        viewModelScope.launch {
            try {
                refreshReposByUser(user)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (e: DataException) {
                if (repos.value.isNullOrEmpty()) {
                    _eventNetworkError.value = true
                }
            }
        }
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    class Factory(
        private val user: String,
        private val refreshReposByUser: RefreshReposByUser,
        private val watchReposByUser: WatchReposByUser
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GithubViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GithubViewModel(user, refreshReposByUser, watchReposByUser) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}