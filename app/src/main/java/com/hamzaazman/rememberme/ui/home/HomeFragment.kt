package com.hamzaazman.rememberme.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamzaazman.rememberme.R
import com.hamzaazman.rememberme.common.viewBinding
import com.hamzaazman.rememberme.databinding.FragmentHomeBinding
import com.hamzaazman.rememberme.ui.home.adapter.ReminderListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var reminderListAdapter: ReminderListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {}
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        reminderListAdapter = ReminderListAdapter(
            onItemClicked = { reminder ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToAddFragment(
                        reminder.id,
                        reminder.title
                    )
                findNavController().navigate(action)


            },
            onCompletedChanged = { reminder, isChecked ->
                viewModel.updateReminderCompletion(reminder, isChecked)
                // TODO: Bildirimleri iptal et/zamanla (AlarmManager kısmı)
                if (isChecked) {
                    viewModel.cancelReminder(reminder)
                } else {
                    viewModel.scheduleReminder(reminder)
                }
            }
        )

        binding.recyclerViewReminders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderListAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddReminder.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddFragment(0)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    reminderListAdapter.submitList(uiState.reminders)

                    binding.progressBarLoading.visibility =
                        if (uiState.isLoading) View.VISIBLE else View.GONE
                    binding.textViewError.visibility =
                        if (uiState.error != null) View.VISIBLE else View.GONE
                    binding.textViewError.text = uiState.error

                    // Eğer hatırlatma listesi boşsa ve hata yoksa, kullanıcıya bilgi verebiliriz
                    if (!uiState.isLoading && uiState.error == null && uiState.reminders.isEmpty()) {
                        // TODO: Boş liste mesajı göstermek için bir TextView ekleyebiliriz

                        // binding.textViewEmptyListMessage.visibility = View.VISIBLE
                    } else {
                        // binding.textViewEmptyListMessage.visibility = View.GONE
                    }

                    uiState.error?.let { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        viewModel.clearErrorMessage()
                    }
                }
            }
        }
    }

}