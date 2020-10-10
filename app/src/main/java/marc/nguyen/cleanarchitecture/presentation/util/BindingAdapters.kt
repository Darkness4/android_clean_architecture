package marc.nguyen.cleanarchitecture.presentation.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import marc.nguyen.cleanarchitecture.core.result.Result
import marc.nguyen.cleanarchitecture.core.result.doOnFailure
import marc.nguyen.cleanarchitecture.core.result.doOnSuccess
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.presentation.ui.adapters.GithubAdapter

@BindingAdapter("state")
fun bindGithubAdapter(
    recyclerView: RecyclerView,
    result: Result<List<Repo>>?
) {
    val adapter = recyclerView.adapter as GithubAdapter
    result?.let { adapter.submitList(it.valueOrNull() ?: emptyList()) }
}

@BindingAdapter("showOnLoading")
fun showOnLoading(view: View, result: Result<List<Repo>>?) {
    view.visibility = result?.let { View.GONE } ?: View.VISIBLE
}

@BindingAdapter("showOnValue")
fun showOnValue(view: View, result: Result<List<Repo>>?) {
    view.visibility = result?.doOnSuccess { View.VISIBLE } ?: View.GONE
}

@BindingAdapter("showOnError")
fun showIfError(view: View, result: Result<List<Repo>>?) {
    view.visibility = result?.doOnFailure { View.VISIBLE } ?: View.GONE
}

@BindingAdapter("showOnError")
fun showIfError(view: TextView, result: Result<List<Repo>>?) {
    result?.doOnFailure {
        view.visibility = View.VISIBLE
        view.text = it.localizedMessage
    } ?: run {
        view.visibility = View.GONE
        view.text = ""
    }
}
