package ru.dl.checklist.app.presenter.zone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.R
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
            val percent = item.percent.toInt()
            txtPercent.text = "$percent%"
            progressBar.progress = percent

            val color = when (percent) {
                in 0 until 75 -> R.color.category_2
                in 75 until 85 -> R.color.category_3
                in 85 until 94 -> R.color.category_4
                else -> R.color.category_5
            }
            progressBar.setIndicatorColor(
                ResourcesCompat.getColor(
                    binding.root.resources,
                    color,
                    null
                )
            )
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