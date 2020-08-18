package marc.nguyen.cleanarchitecture.presentation.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import marc.nguyen.cleanarchitecture.R
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
                val inputMethodManager =
                    context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                viewModel.navigateToGithub(user)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.username_is_empty_toast),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.repositoryInputEditText.doAfterTextChanged {
            if (viewModel.user.value.isNullOrBlank()) {
                binding.repositoryInput.error = getString(R.string.username_is_empty_error)
            } else {
                binding.repositoryInput.error = null
            }
        }
        viewModel.navigateToGithub.observe(
            viewLifecycleOwner,
            {
                if (it != null) {
                    findNavController().navigate(
                        QueryFragmentDirections.actionQueryFragmentToGithubFragment(it)
                    )
                    viewModel.navigateToGithubDone()
                }
            }
        )
        return binding.root
    }
}
