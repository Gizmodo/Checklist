package ru.dl.checklist.app.presenter.auth

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.textChanges
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentAuthBinding
import ru.dl.checklist.domain.model.UserDomain
import timber.log.Timber

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding by viewLifecycleLazy(FragmentAuthBinding::bind)

    private val viewModel: AuthViewModel by lazy {
        getViewModel { AuthViewModel() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initViewModelObservers()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initUI() {
        binding.btnEnter.setOnClickListener {
            viewModel.event(AuthContract.Event.OnLogin)
        }
        binding.edtPassword.textChanges()
            .onEach {
                Timber.i("password length = ${it?.length}")
                viewModel.event(AuthContract.Event.onPasswordChange(password = it.toString()))
            }.launchIn(lifecycleScope)
    }

    private fun setUsersAdapter(view: AutoCompleteTextView, items: List<UserDomain>) {
        val names: AbstractList<String?> = object : AbstractList<String?>() {
            override fun get(index: Int): String = items[index].user

            override val size: Int = items.size
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
        view.setAdapter(adapter)
        view.setOnItemClickListener { _, _, position, _ ->
            viewModel.event(
                AuthContract.Event.onUsernameChange(
                    username = items[position].user,
                    group = items[position].group
                )
            )
        }
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.effect) {
            when (it) {
                is AuthContract.Effect.ShowMessage -> Toast.makeText(
                    requireContext(),
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        collectLatestLifecycleFlow(viewModel.state) { state ->
            binding.progressBar.visibility = (if (state.isLoading) View.VISIBLE else View.GONE)
            Timber.i(state.isLoginButtonEnabled.toString())
            binding.btnEnter.isEnabled = state.isLoginButtonEnabled
            setUsersAdapter(binding.edtUsername, state.usersList)
            Timber.i(state.usersList.toString())
        }
    }
}