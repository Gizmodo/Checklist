package ru.dl.checklist.app.presenter.zone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.dl.checklist.R

class ZonesListFragment : Fragment() {

    companion object {
        fun newInstance() = ZonesListFragment()
    }

    private lateinit var viewModel: ZonesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_zones_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ZonesListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}