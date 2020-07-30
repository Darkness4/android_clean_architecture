package marc.nguyen.cleanarchitecture.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import marc.nguyen.cleanarchitecture.core.exception.DataException
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser

class GithubViewModel(
    user: String,
    private val refreshReposByUser: RefreshReposByUser,
    watchReposByUser: WatchReposByUser
) : ViewModel() {
    sealed class State {
        object Loaded : State()
        data class Error(val e: DataException) : State()
        object Loading : State()
    }

    // RefreshData's state
    private var _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    // Used for toast
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    val repos = watchReposByUser(user)
        .onEach {
            if (!it.isNullOrEmpty()) {
                _state.value = State.Loaded
            }
        }
        .asLiveData(Dispatchers.Main + viewModelScope.coroutineContext)

    init {
        refreshDataFromRepository(user)
    }

    private fun refreshDataFromRepository(user: String) {
        viewModelScope.launch {
            try {
                _state.value = State.Loading
                refreshReposByUser(user)
                _isNetworkErrorShown.value = false
            } catch (e: DataException) {
                if (repos.value.isNullOrEmpty()) {
                    _state.value = State.Error(e)
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