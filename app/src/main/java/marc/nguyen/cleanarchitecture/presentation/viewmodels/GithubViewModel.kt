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
import kotlinx.coroutines.flow.map
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser
import marc.nguyen.cleanarchitecture.presentation.util.State

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