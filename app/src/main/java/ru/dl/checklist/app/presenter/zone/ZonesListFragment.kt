package ru.dl.checklist.app.presenter.zone

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentZonesListBinding
import timber.log.Timber

class ZonesListFragment : Fragment(R.layout.fragment_zones_list) {

    companion object {
        fun newInstance() = ZonesListFragment()
    }

    val args: ZonesListFragmentArgs by navArgs()
    private val binding by viewLifecycleLazy(FragmentZonesListBinding::bind)
    private val viewModel: ZonesListViewModel by lazy {
        getViewModel { ZonesListViewModel() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val checklistUUID = args.checklistUUID
        Timber.i(checklistUUID)
        initUI()
        initViewModelObservers()
        viewModel.onEvent(ZoneListEvent.LoadZoneListByCategory(checklistUUID))
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.zoneListEvent) {
            Timber.i("Collected data from ZoneFragment")
            Timber.d(it.toString())
        }
    }

    private fun initUI() {
    }

}