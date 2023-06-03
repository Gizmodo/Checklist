package ru.dl.checklist.app.presenter.template

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardTemplateBinding
import ru.dl.checklist.domain.model.TemplateDomain

class TemplateAdapter(
    private val onItemClick: (item: TemplateDomain) -> Unit
) : ListAdapter<TemplateDomain, TemplateAdapter.TemplateViewHolder>(DiffCallback) {
    private val list = mutableListOf<TemplateDomain>()

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TemplateDomain>() {
            override fun areItemsTheSame(
                oldItem: TemplateDomain,
                newItem: TemplateDomain
            ) = oldItem.uuid == newItem.uuid

            override fun areContentsTheSame(
                oldItem: TemplateDomain,
                newItem: TemplateDomain
            ) = oldItem == newItem

        }
    }

    fun updateList(newItems: List<TemplateDomain>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class TemplateViewHolder(private val binding: CardTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TemplateDomain): CardTemplateBinding {
            binding.txtTemplateName.text = item.name
            binding.root.setOnClickListener { onItemClick(item) }
            return binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder =
        TemplateViewHolder(
            CardTemplateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}