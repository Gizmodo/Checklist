package ru.dl.checklist.app.presenter.house.check

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.PageItemBinding
import ru.dl.checklist.domain.model.HouseCheckDomain

class HouseCheckAdapter(
    private val onNoClick: (item: HouseCheckDomain) -> Unit,
    private val onYesClick: (item: HouseCheckDomain) -> Unit,
    private val onClickAddPhoto: (item: HouseCheckDomain) -> Unit,
) :
    RecyclerView.Adapter<HouseCheckAdapter.PageHolder>() {
    private val list = mutableListOf<HouseCheckDomain>()

    inner class PageHolder(private val binding: PageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HouseCheckDomain) = binding.apply {
            txtQuestion.text = item.name
            btnNo.setOnClickListener { onNoClick(item) }
            btnYes.setOnClickListener { onYesClick(item) }
            btnAttach.setOnClickListener { onClickAddPhoto(item) }

            badgeOuter.visibility = (if (item.isPhotoRequired) View.VISIBLE else View.GONE)

            when {
                item.mediacount > 0 -> {
                    txtCount.visibility = View.VISIBLE
                    txtCount.text = item.mediacount.toString()
                }

                else -> binding.txtCount.visibility = View.GONE
            }

            when (item.answer) {
                true -> btnYes.isChecked = true
                false -> btnNo.isChecked = true
                null -> radioGroup.clearCheck()
            }
        }
    }

    fun getItemAtPosition(position: Int) = list[position]

    fun updateList(newItems: List<HouseCheckDomain>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PageHolder(
            PageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        holder.bind(list[position])
    }
}