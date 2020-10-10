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
import androidx.navigation.fragment.navArgs
import arrow.core.getOrHandle
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import marc.nguyen.cleanarchitecture.databinding.GithubFragmentBinding
import marc.nguyen.cleanarchitecture.presentation.ui.adapters.GithubAdapter
import marc.nguyen.cleanarchitecture.presentation.viewmodels.GithubViewModel
import javax.inject.Inject

@AndroidEntryPoint
class GithubFragment : Fragment() {
    private val args by navArgs<GithubFragmentArgs>()

    @Inject
    lateinit var viewModelInteractors: Lazy<GithubViewModel.Interactors>

    private val viewModel by viewModels<GithubViewModel> {
        GithubViewModel.Factory(viewModelInteractors.get(), args.user)
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
        binding.repoList.adapter = GithubAdapter {
            openHtmlUrl(it.htmlUrl)
        }
        viewModel.networkStatus.observe(
            viewLifecycleOwner,
            { result ->
                result?.getOrHandle {
                    Toast.makeText(
                        context,
                        it.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
                viewModel.manualRefreshDone()
            }
        )

        return binding.root
    }

    private fun openHtmlUrl(htmlUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(htmlUrl))
        startActivity(intent)
    }
}
