package marc.nguyen.cleanarchitecture.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QueryViewModel : ViewModel() {
    val user = MutableLiveData<String>()

    private val _navigateToGithub = MutableLiveData<String?>()
    val navigateToGithub: LiveData<String?>
        get() = _navigateToGithub

    fun navigateToGithub(user: String) {
        _navigateToGithub.value = user
    }

    fun navigateToGithubDone() {
        _navigateToGithub.value = null
    }
}