package ru.dl.checklist.app.presenter.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.navigateExt
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentDetailBinding
import ru.dl.checklist.domain.model.ChecklistDomain
import timber.log.Timber

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private val args: DetailFragmentArgs by navArgs()
    private val binding by viewLifecycleLazy(FragmentDetailBinding::bind)
    private val viewModel: DetailViewModel by lazy {
        getViewModel { DetailViewModel() }
    }
    private var detailChecklistAdapter = DetailChecklistAdapter(::onItemClick)

    private fun onItemClick(item: ChecklistDomain) {
        viewModel.event(DetailContract.Event.OnItemClick(item))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.address = args.address
        Timber.i("address ${args.address}")
        initUI()
        initViewModelObservers()
        viewModel.event(DetailContract.Event.OnRefresh)
    }

    private fun initUI() {
        with(binding.rvChecklists) {
            val animator = itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = detailChecklistAdapter
        }

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

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.state) { state ->
            Timber.i("Загружено чеклистов: ${state.list.count()} шт.")
            if (state.list.isNotEmpty()) {
                detailChecklistAdapter.updateList(state.list)
            }
        }
        collectLatestLifecycleFlow(viewModel.effect) { effect ->
            when (effect) {
                is DetailContract.Effect.Navigate -> {
                    Timber.i("Переход на ${effect.direction}")
                    when (effect.direction) {
                        is NavigationRouteDetail.RouteDetailToZone -> {
                            navigateExt(
                                DetailFragmentDirections.actionDetailFragmentToZonesListFragment(
                                    effect.direction.checklistUUID
                                )
                            )
                        }
                    }
                }


                is DetailContract.Effect.ShowMessage -> Toast.makeText(
                    requireContext(), effect.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}