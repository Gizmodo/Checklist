package ru.dl.checklist.app.presenter.objects

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.navigateExt
import ru.dl.checklist.app.ext.textChanges
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentObjectsBinding
import ru.dl.checklist.domain.model.ObjectDomain

class ObjectsFragment : Fragment(R.layout.fragment_objects) {
    private val binding by viewLifecycleLazy(FragmentObjectsBinding::bind)
    private val viewModel: ObjectsViewModel by lazy { getViewModel { ObjectsViewModel() } }
    private lateinit var swipe: SwipeRefreshLayout
    private var objectAdapter = ObjectAdapter(onItemClick = ::onItemClick)
    private fun onItemClick(item: ObjectDomain) {
        navigateExt(
            ObjectsFragmentDirections.actionObjectsFragmentToChecklistTemplateFragment(
                /* objectUuid = */ item.uuid,
                /* objectName = */ item.name
            )
        )

    }

    private fun isProgressVisible(isVisible: Boolean) {
        when (isVisible) {
            true -> {
                binding.rvList.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.txtCount.visibility = View.GONE
            }

            false -> {
                binding.rvList.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.txtCount.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.effect) {
            when (it) {
                ObjectsListContract.Effect.OnBackPressed -> {

                }

                is ObjectsListContract.Effect.ShowToast -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        collectLatestLifecycleFlow(viewModel.state) { state ->
            isProgressVisible(state.refreshing)
            objectAdapter.updateList(state.objectsList)
            binding.txtCount.text = "Найдено: ${state.objectsList.size} шт."
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initUI() {
        swipe = binding.swipeLayout
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
            viewModel.event(ObjectsListContract.Event.OnRefresh)
        }

        with(binding.rvList) {
            val animator = itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = objectAdapter
        }
        binding.edtSearch
            .textChanges()
            .map { it.toString() }
            .onEach { viewModel.event(ObjectsListContract.Event.OnSearch(it)) }
            .launchIn(lifecycleScope)
    }

}