package ru.dl.checklist.app.presenter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardObjectBinding
import ru.dl.checklist.domain.model.ChecklistGroupedByAddressDomain

class MainObjectAdapter(private val onItemClick: (item: ChecklistGroupedByAddressDomain) -> Unit) :
    ListAdapter<ChecklistGroupedByAddressDomain, MainObjectAdapter.ObjectViewHolder>(DiffCallback) {
    inner class ObjectViewHolder(private val binding: CardObjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChecklistGroupedByAddressDomain): CardObjectBinding {
            binding.txtTemplateName.text = item.shortname
            binding.txtShortName.text = item.address
            binding.root.setOnClickListener { onItemClick(item) }
            return binding
        }
    }

    private val list = mutableListOf<ChecklistGroupedByAddressDomain>()

    companion object {
        private val DiffCallback =
            object : DiffUtil.ItemCallback<ChecklistGroupedByAddressDomain>() {
                override fun areItemsTheSame(
                    oldItem: ChecklistGroupedByAddressDomain,
                    newItem: ChecklistGroupedByAddressDomain
                ) = oldItem.address == newItem.address

                override fun areContentsTheSame(
                    oldItem: ChecklistGroupedByAddressDomain,
                    newItem: ChecklistGroupedByAddressDomain
                ) = oldItem == newItem

            }
    }

    fun updateList(newItems: List<ChecklistGroupedByAddressDomain>) {
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
