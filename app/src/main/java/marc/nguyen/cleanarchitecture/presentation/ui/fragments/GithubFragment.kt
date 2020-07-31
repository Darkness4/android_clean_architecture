package marc.nguyen.cleanarchitecture.presentation.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
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

        return binding.root
    }

    private fun onClick(repo: Repo) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
        startActivity(intent)
    }
}