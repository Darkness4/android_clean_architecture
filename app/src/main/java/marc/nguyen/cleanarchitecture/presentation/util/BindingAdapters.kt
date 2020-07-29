package marc.nguyen.cleanarchitecture.presentation.util

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import marc.nguyen.cleanarchitecture.domain.entities.Repo
import marc.nguyen.cleanarchitecture.presentation.ui.adapters.GithubAdapter

@BindingAdapter("isNetworkError", "data")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, data: Any?) {
    view.visibility = if (data != null) View.GONE else View.VISIBLE

    if (isNetWorkError) {
        view.visibility = View.GONE
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(
    recyclerView: RecyclerView,
    data: List<Repo>?
) {
    val adapter = recyclerView.adapter as GithubAdapter
    adapter.submitList(data)
}