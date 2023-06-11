package ru.dl.checklist.app.presenter.house.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardHouseBinding
import ru.dl.checklist.domain.model.HouseChecklistDomain

class HouseAdapter(
    private val list: MutableList<HouseChecklistDomain>,
    private val onItemClick: (HouseChecklistDomain) -> Unit
) : RecyclerView.Adapter<HouseAdapter.ChecklistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardHouseBinding.inflate(inflater, parent, false)
        return ChecklistViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun updateList(items: List<HouseChecklistDomain>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    inner class ChecklistViewHolder(private val binding: CardHouseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HouseChecklistDomain) = binding.apply {
            txtTitle.text = item.title
            txtStart.text = item.timeStart
            txtEnd.text = item.timeEnd
            root.setOnClickListener { onItemClick(item) }
        }
    }
}