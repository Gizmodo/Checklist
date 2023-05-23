package ru.dl.checklist.app.presenter.bottomsheet

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.parcelable
import ru.dl.checklist.app.ext.viewBottomSheetLifecycleLazy
import ru.dl.checklist.app.presenter.mark.MarkListEvent
import ru.dl.checklist.app.presenter.mark.MarksListViewModel
import ru.dl.checklist.databinding.FragmentBottomSheetBinding
import ru.dl.checklist.domain.model.MarkDomainWithCount

class BottomSheetFragment :
    BottomSheetDialogFragment(R.layout.fragment_bottom_sheet) {

    private val binding by viewBottomSheetLifecycleLazy(FragmentBottomSheetBinding::bind)
    private val viewModel: MarksListViewModel by lazy { getViewModel { MarksListViewModel() } }
    private lateinit var behavior: BottomSheetBehavior<View>
    lateinit var item: MarkDomainWithCount
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item = arguments?.parcelable<MarkDomainWithCount>("item")!!
        behavior = BottomSheetBehavior.from(requireView().parent as View)

        // Настройки Bottom Sheet
        behavior.peekHeight = 10000
        behavior.skipCollapsed = true
        //behavior.disableShapeAnimations()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        initUI(item)
    }

    private fun initUI(item: MarkDomainWithCount) {
        with(binding) {
            slider.value = item.answer.toFloat()
            edtComment.setText(item.comment)
            edtPKD.setText(item.pkd)
            btnDiscard.setOnClickListener { dialog?.dismiss() }
            btnSave.setOnClickListener {
                updateItem()
                dialog?.dismiss()
            }
        }
    }

    private fun updateItem() {
        val answer = binding.slider.value
        val comment = binding.edtComment.text.toString()
        val pkd = binding.edtPKD.text.toString()
        viewModel.onEvent(event = MarkListEvent.ChangeMark(item.id, comment, answer, pkd))
    }
}