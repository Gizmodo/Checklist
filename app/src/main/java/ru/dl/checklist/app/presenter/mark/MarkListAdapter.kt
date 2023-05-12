package ru.dl.checklist.app.presenter.mark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardMarkBinding
import ru.dl.checklist.domain.model.MarkDomain

class MarkListAdapter(
    private val list: MutableList<MarkDomain>,
    private val onItemClick: (MarkDomain) -> Unit
) : RecyclerView.Adapter<MarkListAdapter.MarkListViewHolder>() {
    inner class MarkListViewHolder(private val binding: CardMarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarkDomain) = binding.apply {
            //  txtZone.text = item.zone
            txtMarkTitle.text = item.title
            root.setOnClickListener { onItemClick(item) }
        }
    }

    fun updateList(items: List<MarkDomain>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarkListAdapter.MarkListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardMarkBinding.inflate(inflater, parent, false)
        return MarkListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarkListAdapter.MarkListViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}