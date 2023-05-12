package ru.dl.checklist.app.presenter.mark

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentMarksListBinding
import ru.dl.checklist.domain.model.MarkDomain
import timber.log.Timber

class MarksListFragment : Fragment(R.layout.fragment_marks_list) {

    companion object {
        fun newInstance() = MarksListFragment()
    }

    private val args: MarksListFragmentArgs by navArgs()
    private val binding by viewLifecycleLazy(FragmentMarksListBinding::bind)
    private val viewModel: MarksListViewModel by lazy {
        getViewModel { MarksListViewModel() }
    }
    private var markListAdapter = MarkListAdapter(mutableListOf(), ::onItemClick)

    private fun onItemClick(markDomain: MarkDomain) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val zoneId = args.zoneId.toLong()
        Timber.i(zoneId.toString())
        initUI()
        initViewModelObservers()
        viewModel.onEvent(MarkListEvent.LoadMarkListByZone(zoneId))
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.markListEvent) {
            Timber.i("Collected data from MarksListFragment")
            Timber.d(it.toString())
            markListAdapter.updateList(it)
        }
    }

    private fun initUI() {
        with(binding.rvMarkList) {
            val animator = itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = markListAdapter
        }
    }
}