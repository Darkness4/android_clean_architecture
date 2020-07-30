package marc.nguyen.cleanarchitecture.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import marc.nguyen.cleanarchitecture.databinding.GithubRepoItemBinding
import marc.nguyen.cleanarchitecture.domain.entities.Repo

class GithubAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Repo, GithubAdapter.ViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(
            oldItem: Repo,
            newItem: Repo
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Repo,
            newItem: Repo
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ViewHolder(
        private var binding:
        GithubRepoItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Repo) {
            binding.repo = repo
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (repo: Repo) -> Unit) {
        fun onClick(repo: Repo) = clickListener(repo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            GithubRepoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(repo)
        }
        holder.bind(repo)
    }
}