package ru.dl.checklist.app.presenter.house.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentHouseBinding
import ru.dl.checklist.domain.model.HouseChecklistDomain

class HouseFragment : Fragment(R.layout.fragment_house) {
    private val binding by viewLifecycleLazy(FragmentHouseBinding::bind)
    private val viewModel: HouseViewModel by lazy {
        getViewModel { HouseViewModel() }
    }
    private var checklistAdapter = HouseAdapter(mutableListOf(), ::onItemClick)

    private fun onItemClick(item: HouseChecklistDomain) {
        // TODO: navigate next screen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initViewModelObservers()
    }

    private fun initUI() {
        with(binding.rvChecklists) {
            val animator = itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = checklistAdapter
        }
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.state) {
            checklistAdapter.updateList(it.list)
        }
    }

}