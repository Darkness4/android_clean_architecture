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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import marc.nguyen.cleanarchitecture.core.exception.DataException
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser

class GithubViewModel @AssistedInject constructor(
    @Assisted private val user: String,
    private val refreshReposByUser: RefreshReposByUser,
    watchReposByUser: WatchReposByUser
) : ViewModel() {
    sealed class State {
        object Loaded : State()
        data class Error(val e: Exception) : State()
        object Loading : State()
    }

    // RefreshData's state
    private var _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    val repos = watchReposByUser(user)
        .onEach {
            if (!it.isNullOrEmpty()) {
                _state.value = State.Loaded
            }
        }
        .catch {
            when (it) {
                is DataException -> _state.value = State.Error(it)
                is Exception -> _state.value = State.Error(it)
                else -> throw it
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
            } catch (e: DataException) {
                if (repos.value.isNullOrEmpty()) {
                    _state.value = State.Error(e)
                }
            }
        }
    }

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(user: String): GithubViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: AssistedFactory,
            user: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(user) as T
            }
        }
    }
}