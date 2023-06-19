package ru.dl.checklist.app.presenter.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.navigateExt
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.app.utils.SD
import ru.dl.checklist.databinding.FragmentMainBinding
import ru.dl.checklist.domain.model.ChecklistDomain
import timber.log.Timber

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding by viewLifecycleLazy(FragmentMainBinding::bind)
    private val viewModel: MainViewModel by lazy {
        getViewModel { MainViewModel() }
    }
    private var checklistAdapter = ChecklistAdapter(mutableListOf(), ::onItemClick)

    private fun onItemClick(item: ChecklistDomain) {
        navigateExt(MainFragmentDirections.actionMainFragmentToZonesListFragment(item.uuid))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        viewModel.onEvent(ChecklistEvent.LoadChecklist)
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
        binding.fabAddChecklist.setOnClickListener {
            navigateExt(MainFragmentDirections.actionMainFragmentToObjectsFragment())
        }
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.checklistEvent) {
            when (it) {
                is SD.Error -> {
                    Timber.e(it.msg)
                }

                SD.Loading -> {
                    Timber.i("Загрузка чеклистов")
                }

                is SD.Success -> {
                    Timber.i("Загружено чеклистов: ${it.result.count()} шт.")
                    checklistAdapter.updateList(it.result)
                }
            }
        }
    }
}