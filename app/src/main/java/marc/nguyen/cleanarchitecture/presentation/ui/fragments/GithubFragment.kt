package marc.nguyen.cleanarchitecture.presentation.ui.fragments

import android.content.Intent
import android.net.Uri
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
import marc.nguyen.cleanarchitecture.core.exception.DataException
import marc.nguyen.cleanarchitecture.databinding.GithubFragmentBinding
import marc.nguyen.cleanarchitecture.domain.entities.Repo
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

    private val args by navArgs<GithubFragmentArgs>()

    private val viewModel by viewModels<GithubViewModel>() {
        GithubViewModel.Factory(args.user, refreshReposByUser, watchReposByUser)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Bind
        val binding: GithubFragmentBinding = GithubFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.repoList.adapter = GithubAdapter(GithubAdapter.OnClickListener {
            onClick(it)
        })

        // Observe
        viewModel.state.observe(
            viewLifecycleOwner,
            Observer<GithubViewModel.State> {
                when (it) {
                    GithubViewModel.State.Loading -> onLoading()
                    GithubViewModel.State.Loaded -> onLoaded()
                    is GithubViewModel.State.Error -> onNetworkError(it.e)
                }
            })

        return binding.root
    }

    private fun onLoaded() {
    }

    private fun onLoading() {
    }

    private fun onNetworkError(e: DataException) {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    private fun onClick(repo: Repo) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
        startActivity(intent)
    }
}