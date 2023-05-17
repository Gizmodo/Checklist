package ru.dl.checklist.app.presenter.mark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import ru.dl.checklist.app.ext.findBy
import ru.dl.checklist.databinding.CardMarkBinding
import ru.dl.checklist.domain.model.Answer
import ru.dl.checklist.domain.model.MarkDomain

class MarkListAdapter(
    private val list: MutableList<MarkDomain>,
    private val onCardUIEvent: (MarkCardUIEvent) -> Unit
) : RecyclerView.Adapter<MarkListAdapter.MarkListViewHolder>() {
    inner class MarkListViewHolder(private val binding: CardMarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarkDomain) = binding.apply {
//            UI
            when ((Answer::value findBy item.answer) ?: Answer.UNDEFINED) {
                Answer.YES -> btnYes.isChecked = true
                Answer.NO -> btnNo.isChecked = true
                Answer.UNDEFINED -> tbMarkAnswer.clearCheck()
            }
            edtMarkComment.setText(item.comment)
            txtMarkTitle.text = item.title

//            Click actions
            btnYes.setOnClickListener { onCardUIEvent(MarkCardUIEvent.Yes(item)) }
            btnNo.setOnClickListener { onCardUIEvent(MarkCardUIEvent.No(item)) }
            btnAttach.setOnClickListener { onCardUIEvent(MarkCardUIEvent.Attach(item)) }
            edtMarkComment.doOnTextChanged { text, start, before, count ->
                onCardUIEvent(MarkCardUIEvent.Comment(text.toString(), item))
            }
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
    ): MarkListAdapter.MarkListViewHolder = MarkListViewHolder(
        CardMarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MarkListAdapter.MarkListViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}