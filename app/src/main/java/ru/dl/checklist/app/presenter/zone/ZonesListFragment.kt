package ru.dl.checklist.app.presenter.zone

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.navigateExt
import ru.dl.checklist.app.ext.snack
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.databinding.FragmentZonesListBinding
import ru.dl.checklist.domain.model.ZoneDomain
import timber.log.Timber

class ZonesListFragment : Fragment(R.layout.fragment_zones_list) {

    private val args: ZonesListFragmentArgs by navArgs()
    private val binding by viewLifecycleLazy(FragmentZonesListBinding::bind)
    private val viewModel: ZonesListViewModel by lazy {
        getViewModel { ZonesListViewModel() }
    }
    private var zoneListAdapter = ZoneListAdapter(mutableListOf(), ::onItemClick)

    private fun onItemClick(zone: ZoneDomain) {
        navigateExt(ZonesListFragmentDirections.actionZonesListFragmentToMarksListFragment(zone.id.toInt()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val checklistUUID = args.checklistUUID
        viewModel.uuidArgs = checklistUUID
        Timber.i(checklistUUID)
        initUI()
        initViewModelObservers()
        viewModel.onEvent(ZoneListEvent.LoadZoneListByCategory)
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.zoneListEvent) {
            Timber.i("Collected data from ZoneFragment")
            Timber.d(it.toString())
            zoneListAdapter.updateList(it)
        }
        collectLatestLifecycleFlow(viewModel.uploadMarksChannel) { result ->
            Timber.i("Результат отправки показателей:")
            when (result) {
                is ApiResult.Error -> {
                    binding.fabSend.snack(result._error)
                    Timber.e(result._error)
                }

                ApiResult.Loading -> {
                    Timber.i("Отправка запроса")
                }

                is ApiResult.Success -> {
                    binding.fabSend.snack(result._data.message, Snackbar.LENGTH_SHORT)
                    Timber.i(result._data.message)
                }
            }
        }
        collectLatestLifecycleFlow(viewModel.uploadImagesChannel) { result ->
            Timber.i("Результат отправки изображений:")
            when (result) {
                is ApiResult.Error -> {
                    binding.fabSend.snack(result._error)
                    Timber.e(result._error)
                }

                ApiResult.Loading -> {
                    Timber.i("Отправка запроса")
                }

                is ApiResult.Success -> {
                    binding.fabSend.snack(result._data.message, Snackbar.LENGTH_SHORT)
                    Timber.i(result._data.message)
                }
            }
        }
    }

    private fun initUI() {
        with(binding.rvZonesLists) {
            val animator = itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = zoneListAdapter
        }
        binding.fabSend.setOnClickListener {
            viewModel.onEvent(ZoneListEvent.SendChecklist)
        }
    }

}