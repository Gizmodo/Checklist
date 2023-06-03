package ru.dl.checklist.app.presenter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.app.utils.ColorCategory
import ru.dl.checklist.databinding.CardChecklistBinding
import ru.dl.checklist.domain.model.ChecklistDomain

class ChecklistAdapter(
    private val list: MutableList<ChecklistDomain>,
    private val onItemClick: (ChecklistDomain) -> Unit
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
            txtAddress.text = item.address
            txtShortName.text = item.shortName
            txtDate.text = item.auditDate
            txtChecker.text = item.checker
            txtSenior.text = item.senior
            txtTitle.text = item.title
            val percent = item.percent.toInt()
            txtPercent.text = "$percent%"
            progressBar.progress = percent

            progressBar.setIndicatorColor(
                ResourcesCompat.getColor(
                    binding.root.resources,
                    ColorCategory.getColorCategory(percent),
                    null
                )
            )
            root.setOnClickListener { onItemClick(item) }
        }
    }
}