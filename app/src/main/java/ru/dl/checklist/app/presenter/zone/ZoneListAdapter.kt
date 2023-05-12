package ru.dl.checklist.app.presenter.zone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardZoneBinding
import ru.dl.checklist.domain.model.ZoneDomain

class ZoneListAdapter(
    private val list: MutableList<ZoneDomain>,
    private val onItemClick: (ZoneDomain) -> Unit
) : RecyclerView.Adapter<ZoneListAdapter.ZoneListViewHolder>() {
    inner class ZoneListViewHolder(private val binding: CardZoneBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ZoneDomain) = binding.apply {
            txtZone.text = item.zone
            root.setOnClickListener { onItemClick(item) }
        }
    }

    fun updateList(items: List<ZoneDomain>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ZoneListAdapter.ZoneListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardZoneBinding.inflate(inflater, parent, false)
        return ZoneListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ZoneListAdapter.ZoneListViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

}