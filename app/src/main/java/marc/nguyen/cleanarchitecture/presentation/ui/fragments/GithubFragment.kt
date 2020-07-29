package marc.nguyen.cleanarchitecture.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import marc.nguyen.cleanarchitecture.databinding.GithubFragmentBinding
import marc.nguyen.cleanarchitecture.domain.usecases.RefreshReposByUser
import marc.nguyen.cleanarchitecture.domain.usecases.WatchReposByUser
import marc.nguyen.cleanarchitecture.presentation.ui.adapters.GithubAdapter
import marc.nguyen.cleanarchitecture.presentation.viewmodels.GithubViewModel
import javax.inject.Inject

@AndroidEntryPoint
class GithubFragment : Fragment() {
    @Inject
    lateinit var refreshReposByUser: RefreshReposByUser

    @Inject
    lateinit var watchReposByUser: WatchReposByUser

    private val viewModel by viewModels<GithubViewModel>() {
        val args by navArgs<GithubFragmentArgs>()
        GithubViewModel.Factory(args.user, refreshReposByUser, watchReposByUser)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: GithubFragmentBinding = GithubFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.repoList.adapter = GithubAdapter()

        viewModel.eventNetworkError.observe(
            viewLifecycleOwner,
            Observer<Boolean> { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })
        return binding.root
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}