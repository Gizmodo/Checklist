package ru.dl.checklist.app.presenter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardChecklistBinding
import ru.dl.checklist.domain.model.ChecklistDomain

class ChecklistAdapter(
    private val list: MutableList<ChecklistDomain>,
    private val onItemClick: (ChecklistDomain, Int) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardChecklistBinding.inflate(inflater, parent, false)
        return ChecklistViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun updateList(items: List<ChecklistDomain>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    inner class ChecklistViewHolder(private val binding: CardChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChecklistDomain) = binding.apply {
            txtAddress.text = item.address.value
            txtShortName.text = item.shortName.value
            txtDate.text = item.auditDate.value
            txtChecker.text = item.checker.value
            txtSenior.text = item.senior.value
            root.setOnClickListener { onItemClick(item, adapterPosition) }
        }
    }
}