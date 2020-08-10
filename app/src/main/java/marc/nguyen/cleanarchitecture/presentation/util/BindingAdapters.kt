package marc.nguyen.cleanarchitecture.presentation.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.presentation.ui.adapters.GithubAdapter

@BindingAdapter("state")
fun bindGithubAdapter(
    recyclerView: RecyclerView,
    result: Result<List<Repo>>?
) {
    val adapter = recyclerView.adapter as GithubAdapter
    if (result != null) {
        adapter.submitList(result.getOrElse { emptyList() })
    }
}

@BindingAdapter("isLoading")
fun showIfLoading(view: View, result: Result<List<Repo>>?) {
    view.visibility = if (result == null) View.VISIBLE else View.GONE
}

@BindingAdapter("isLoaded")
fun showIfLoaded(view: View, result: Result<List<Repo>>?) {
    view.visibility = if (result != null) {
         result.fold(
            { View.VISIBLE },
            { View.GONE }
        )
    } else {
        View.GONE
    }
}

@BindingAdapter("isError")
fun showIfError(view: View, result: Result<List<Repo>>?) {
    view.visibility = if (result != null) {
        result.fold(
            { View.GONE },
            { View.VISIBLE }
        )
    } else {
        View.GONE
    }
}

@BindingAdapter("isError")
fun showIfError(view: TextView, result: Result<List<Repo>>?) {
    if (result != null) {
        result.fold(
            {
                view.visibility = View.GONE
                view.text = ""
            },
            {
                view.visibility = View.VISIBLE
                view.text = it.localizedMessage
            }
        )
    }
}