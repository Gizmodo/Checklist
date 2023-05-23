package ru.dl.checklist.app.presenter.mark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardMarkBinding
import ru.dl.checklist.domain.model.MarkDomainWithCount

class MarkListAdapter(
    private val onCardUIEvent: (MarkCardUIEvent) -> Unit,
    private val onClickAddPhoto: (item: MarkDomainWithCount) -> Unit,
    private val onMarkClick: (item: MarkDomainWithCount) -> Unit,
) : ListAdapter<MarkDomainWithCount, MarkListAdapter.MarkListViewHolder>(DiffCallback) {
    private val markList = mutableListOf<MarkDomainWithCount>()

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MarkDomainWithCount>() {
            override fun areItemsTheSame(
                oldItem: MarkDomainWithCount,
                newItem: MarkDomainWithCount
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MarkDomainWithCount,
                newItem: MarkDomainWithCount
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class MarkListViewHolder(private val binding: CardMarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarkDomainWithCount): CardMarkBinding {
            //  UI
            binding.txtMarkTitle.text = item.title
            binding.txtMarkComment.text = item.comment
            binding.txtAnswer.text = "${item.answer}/10"
            when {
                item.count > 0 -> {
                    binding.txtCount.visibility = View.VISIBLE
                    binding.txtCount.text = item.count.toString()
                }

                else -> {
                    binding.txtCount.visibility = View.GONE
                }
            }

            //  Click actions
            binding.btnAttach.setOnClickListener { onClickAddPhoto(item) }
            binding.root.setOnClickListener { onMarkClick(item) }


            /* btnYes.setOnClickListener { onCardUIEvent(MarkCardUIEvent.ChangeAnswer(item.copy(answer = Answer.YES))) }
                btnNo.setOnClickListener { onCardUIEvent(MarkCardUIEvent.ChangeAnswer(item.copy(answer = Answer.NO))) }*/
            /* btnYes.setOnClickListener { onClickAnswer }
                btnNo.setOnClickListener { onClickAnswer }*/
            /*
                        binding.btnAttach.setOnClickListener { onCardUIEvent(MarkCardUIEvent.ChangeAttach(item)) }
                        binding.edtMarkComment.doOnTextChanged { text, start, before, count ->
                            onCardUIEvent(MarkCardUIEvent.ChangeComment(item.copy(comment = text.toString())))
                        }*/
            return binding
        }

        /* fun bind_prev(item: MarkDomain): CardMarkBinding {
             binding.root.setOnClickListener { onClickAnswer(item) }
             //            UI
             when (item.answer) {
                 Answer.YES -> binding.btnYes.isChecked = true
                 Answer.NO -> binding.btnNo.isChecked = true
                 Answer.UNDEFINED -> binding.tbMarkAnswer.clearCheck()
             }
             binding.edtMarkComment.apply {
                 setText(item.comment)
                 setSelection(this.text.toString().length)
             }
             binding.txtMarkTitle.text = item.title

             //            Click actions
             *//* btnYes.setOnClickListener { onCardUIEvent(MarkCardUIEvent.ChangeAnswer(item.copy(answer = Answer.YES))) }
                btnNo.setOnClickListener { onCardUIEvent(MarkCardUIEvent.ChangeAnswer(item.copy(answer = Answer.NO))) }*//*
            *//* btnYes.setOnClickListener { onClickAnswer }
                btnNo.setOnClickListener { onClickAnswer }*//*

            binding.btnAttach.setOnClickListener { onCardUIEvent(MarkCardUIEvent.ChangeAttach(item)) }
            binding.edtMarkComment.doOnTextChanged { text, start, before, count ->
                onCardUIEvent(MarkCardUIEvent.ChangeComment(item.copy(comment = text.toString())))
            }
            return binding
        }*/
    }

    fun updateList(newItems: List<MarkDomainWithCount>) {
        markList.clear()
        markList.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarkListAdapter.MarkListViewHolder = MarkListViewHolder(
        CardMarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MarkListAdapter.MarkListViewHolder, position: Int) {
        holder.bind(markList[position])
    }

    override fun getItemCount(): Int = markList.size
}