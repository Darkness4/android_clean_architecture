package marc.nguyen.cleanarchitecture.presentation.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.presentation.ui.adapters.GithubAdapter

@BindingAdapter("listData")
fun bindRecyclerView(
    recyclerView: RecyclerView,
    data: List<Repo>?
) {
    val adapter = recyclerView.adapter as GithubAdapter
    adapter.submitList(data)
}

@BindingAdapter("isLoading")
fun showIfLoading(view: View, state: State?) {
    state?.let {
        view.visibility = if (state is State.Loading) View.VISIBLE else View.GONE
    }
}

@BindingAdapter("isLoaded")
fun showIfLoaded(view: View, state: State?) {
    state?.let {
        view.visibility = if (state is State.Loaded<*>) View.VISIBLE else View.GONE
    }
}

@BindingAdapter("isError")
fun showIfError(view: View, state: State?) {
    state?.let {
        view.visibility = if (state is State.Error) View.VISIBLE else View.GONE
    }
}

@BindingAdapter("isError")
fun showIfError(view: TextView, state: State?) {
    state?.let {
        if (state is State.Error) {
            view.visibility = View.VISIBLE
            view.text = state.e.localizedMessage
        } else {
            view.visibility = View.GONE
            view.text = ""
        }
    }
}