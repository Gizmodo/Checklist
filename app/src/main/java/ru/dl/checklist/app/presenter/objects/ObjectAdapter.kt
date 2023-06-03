package ru.dl.checklist.app.presenter.objects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardObjectBinding
import ru.dl.checklist.domain.model.ObjectDomain

class ObjectAdapter(private val onItemClick: (item: ObjectDomain) -> Unit) :
    ListAdapter<ObjectDomain, ObjectAdapter.ObjectViewHolder>(DiffCallback) {
    inner class ObjectViewHolder(private val binding: CardObjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ObjectDomain): CardObjectBinding {
            binding.txtTemplateName.text = item.name
            binding.txtShortName.text = item.shortname
            binding.root.setOnClickListener { onItemClick(item) }
            return binding
        }
    }

    private val list = mutableListOf<ObjectDomain>()

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ObjectDomain>() {
            override fun areItemsTheSame(
                oldItem: ObjectDomain,
                newItem: ObjectDomain
            ) = oldItem.uuid == newItem.uuid

            override fun areContentsTheSame(
                oldItem: ObjectDomain,
                newItem: ObjectDomain
            ) = oldItem == newItem

        }
    }

    fun updateList(newItems: List<ObjectDomain>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder =
        ObjectViewHolder(
            CardObjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
