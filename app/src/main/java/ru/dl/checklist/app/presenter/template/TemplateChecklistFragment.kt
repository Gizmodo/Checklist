package ru.dl.checklist.app.presenter.template

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.navigateExt
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentChecklistTemplateBinding
import ru.dl.checklist.domain.model.TemplateDomain

class TemplateChecklistFragment : Fragment(R.layout.fragment_checklist_template) {
    private val binding by viewLifecycleLazy(FragmentChecklistTemplateBinding::bind)
    private val viewModel: TemplateViewModel by lazy { getViewModel { TemplateViewModel() } }
    private lateinit var swipe: SwipeRefreshLayout
    private var templateAdapter = TemplateAdapter(onItemClick = ::onItemClick)

    private fun onItemClick(item: TemplateDomain) {
        navigateExt(
            TemplateChecklistFragmentDirections.actionChecklistTemplateFragmentToObjectsFragment(
                /* templateUuid = */ item.uuid,
                /* templateName = */ item.name
            )
        )
    }

    private fun isProgressVisible(isVisible: Boolean) {
        when (isVisible) {
            true -> {
                binding.rvList.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }

            false -> {
                binding.rvList.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
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
                TemplatesListContract.Effect.OnBackPressed -> {

                }

                is TemplatesListContract.Effect.ShowToast -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        collectLatestLifecycleFlow(viewModel.state) { state ->
            isProgressVisible(state.refreshing)
            templateAdapter.updateList(state.templatesList)
        }
    }

    private fun initUI() {
        swipe = binding.swipeLayout
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
            viewModel.event(TemplatesListContract.Event.OnRefresh)
        }

        with(binding.rvList) {
            val animator = itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = templateAdapter
        }
    }
}