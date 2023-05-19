package ru.dl.checklist.app.presenter.mark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.databinding.CardMarkBinding
import ru.dl.checklist.domain.model.Answer
import ru.dl.checklist.domain.model.MarkDomain

class MarkListAdapter(
    private val onCardUIEvent: (MarkCardUIEvent) -> Unit,
    private val onClickAnswer: (markId: Long, answer: Answer) -> Unit,
    private val onClickAddComment: (item: MarkDomain) -> Unit,
    private val onClickAddPhoto: (item: MarkDomain) -> Unit
) : RecyclerView.Adapter<MarkListAdapter.MarkListViewHolder>() {
    private val markList = mutableListOf<MarkDomain>()

    inner class MarkListViewHolder(private val binding: CardMarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarkDomain): CardMarkBinding {
            //  UI
            binding.txtMarkTitle.text = item.title
            binding.txtMarkComment.text = item.comment

            when (item.answer) {
                Answer.YES -> binding.btnYes.isChecked = true
                Answer.NO -> binding.btnNo.isChecked = true
                Answer.UNDEFINED -> binding.tbMarkAnswer.clearCheck()
            }

            //  Click actions
            binding.btnYes.setOnClickListener { onClickAnswer(item.id, Answer.YES) }
            binding.btnNo.setOnClickListener { onClickAnswer(item.id, Answer.NO) }
            binding.btnComment.setOnClickListener { onClickAddComment(item) }
            binding.btnAttach.setOnClickListener { onClickAddPhoto(item) }


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

    fun updateList(newItems: List<MarkDomain>) {
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