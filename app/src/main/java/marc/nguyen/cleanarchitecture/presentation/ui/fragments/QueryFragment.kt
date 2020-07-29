package marc.nguyen.cleanarchitecture.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import marc.nguyen.cleanarchitecture.databinding.QueryFragmentBinding
import marc.nguyen.cleanarchitecture.presentation.viewmodels.QueryViewModel

class QueryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = QueryFragmentBinding.inflate(inflater)
        val viewModel by viewModels<QueryViewModel>()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.findButton.setOnClickListener {
            val user = viewModel.user.value
            if (!user.isNullOrBlank()) {
                viewModel.navigateToGithub(user)
            }
        }
        viewModel.navigateToGithub.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(
                    QueryFragmentDirections.actionQueryFragmentToGithubFragment(it)
                )
                viewModel.navigateToGithubDone()
            }
        })
        return binding.root
    }
}