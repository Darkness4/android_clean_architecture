package marc.nguyen.cleanarchitecture.presentation.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import arrow.core.getOrHandle
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.presentation.ui.adapters.GithubAdapter

@BindingAdapter("state")
fun bindGithubAdapter(
    recyclerView: RecyclerView,
    result: Either<Throwable, List<Repo>>?
) {
    val adapter = recyclerView.adapter as GithubAdapter
    if (result != null) {
        adapter.submitList(result.getOrHandle { emptyList() })
    }
}

@BindingAdapter("isLoading")
fun showIfLoading(view: View, result: Either<Throwable, List<Repo>>?) {
    view.visibility = if (result == null) View.VISIBLE else View.GONE
}

@BindingAdapter("isLoaded")
fun showIfLoaded(view: View, result: Either<Throwable, List<Repo>>?) {
    view.visibility = result?.fold(
        { View.GONE },
        { View.VISIBLE }
    ) ?: View.GONE
}

@BindingAdapter("isError")
fun showIfError(view: View, result: Either<Throwable, List<Repo>>?) {
    view.visibility = result?.fold(
        { View.VISIBLE },
        { View.GONE }
    ) ?: View.GONE
}

@BindingAdapter("isError")
fun showIfError(view: TextView, result: Either<Throwable, List<Repo>>?) {
    if (result != null) {
        result.fold(
            {
                view.visibility = View.VISIBLE
                view.text = it.localizedMessage
            },
            {
                view.visibility = View.GONE
                view.text = ""
            }
        )
    } else {
        view.visibility = View.GONE
        view.text = ""
    }
}
