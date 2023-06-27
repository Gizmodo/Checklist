package ru.dl.checklist.app.presenter.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.navigateExt
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentMainBinding
import ru.dl.checklist.domain.model.ChecklistGroupedByAddressDomain
import timber.log.Timber

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding by viewLifecycleLazy(FragmentMainBinding::bind)
    private val viewModel: MainViewModel by lazy {
        getViewModel { MainViewModel() }
    }
    private var checklistAdapter = MainObjectAdapter(::onItemClick)

    private fun onItemClick(item: ChecklistGroupedByAddressDomain) {
        viewModel.event(MainContract.Event.OnItemClick(item))
    }

    private fun isProgressVisible(isVisible: Boolean) {
        when (isVisible) {
            true -> {
                binding.rvChecklists.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }

            false -> {
                binding.rvChecklists.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
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
        binding.fabAddChecklist.setOnClickListener {
            viewModel.event(MainContract.Event.OnAssignClick)
        }
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.state) { state ->
            isProgressVisible(state.loading)
            Timber.i("Загружено объектов: ${state.list.count()} шт.")
            checklistAdapter.updateList(state.list)
        }
        collectLatestLifecycleFlow(viewModel.effect) { effect ->
            when (effect) {
                is MainContract.Effect.Navigate -> {
                    Timber.i("Переход на ${effect.direction}")
                    when (effect.direction) {
                        NavigationRouteMain.RouteAssignChecklist -> {
                            navigateExt(MainFragmentDirections.actionMainFragmentToObjectsFragment())
                        }

                        is NavigationRouteMain.RouteDetailChecklist -> {
                            navigateExt(
                                MainFragmentDirections.actionMainFragmentToDetailFragment(
                                    effect.direction.address
                                )
                            )
                        }
                    }
                }

                is MainContract.Effect.ShowMessage -> {
                    Toast.makeText(
                        requireContext(), effect.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}