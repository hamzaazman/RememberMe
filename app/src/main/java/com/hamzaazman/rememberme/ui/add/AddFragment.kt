package com.hamzaazman.rememberme.ui.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hamzaazman.rememberme.R
import com.hamzaazman.rememberme.common.viewBinding
import com.hamzaazman.rememberme.data.model.ReminderPriority
import com.hamzaazman.rememberme.databinding.FragmentAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar


@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {

    private val binding by viewBinding(FragmentAddBinding::bind)

    private val viewModel by viewModels<AddViewModel>()

    private val args: AddFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.reminderId != 0) {
            activity?.title = getString(R.string.edit_reminder_title)
        } else {
            activity?.title = getString(R.string.add_reminder_title)
        }

        setupListeners()
        observeViewModel()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        binding.etDate.setOnClickListener { showDatePicker() }
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }
        binding.etTime.setOnClickListener { showTimePicker() }
        binding.tilTime.setEndIconOnClickListener { showTimePicker() }


        binding.etTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.updateTitle(text.toString())
        }
        binding.etDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.updateDescription(text.toString())
        }

        binding.radioGroupPriority.setOnCheckedChangeListener { _, checkedId ->
            val selectedPriority = when (checkedId) {
                R.id.radio_low_priority -> ReminderPriority.LOW
                R.id.radio_medium_priority -> ReminderPriority.MEDIUM
                R.id.radio_high_priority -> ReminderPriority.HIGH
                else -> ReminderPriority.LOW
            }
            viewModel.updatePriority(selectedPriority)
        }

        binding.btnSaveReminder.setOnClickListener {
            viewModel.saveReminder()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->

                    if (binding.etTitle.text.toString() != uiState.title) {
                        binding.etTitle.setText(uiState.title)
                        binding.etTitle.setSelection(uiState.title.length)
                    }

                    if (binding.etDescription.text.toString() != uiState.description) {
                        binding.etDescription.setText(uiState.description)
                        binding.etDescription.setSelection(uiState.description.length)
                    }


                    val formattedDate = viewModel.getFormattedDate(uiState.dateTimeMillis)
                    val formattedTime = viewModel.getFormattedTime(uiState.dateTimeMillis)

                    if (binding.etDate.text.toString() != formattedDate) {
                        binding.etDate.setText(formattedDate)
                    }
                    if (binding.etTime.text.toString() != formattedTime) {
                        binding.etTime.setText(formattedTime)
                    }

                    val selectedRadioBtnId = when (uiState.selectedPriority) {
                        ReminderPriority.LOW -> R.id.radio_low_priority
                        ReminderPriority.MEDIUM -> R.id.radio_medium_priority
                        ReminderPriority.HIGH -> R.id.radio_high_priority
                    }
                    if (binding.radioGroupPriority.checkedRadioButtonId != selectedRadioBtnId) {
                        binding.radioGroupPriority.check(selectedRadioBtnId)
                    }

                    binding.progressBarAddEditLoading.visibility =
                        if (uiState.isLoading) View.VISIBLE else View.GONE
                    binding.btnSaveReminder.isEnabled = !uiState.isLoading

                    uiState.error?.let { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        viewModel.resetUiStateFlags()
                    }

                    if (uiState.isSaved) {
                        Toast.makeText(
                            context,
                            getString(R.string.reminder_saved),
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.resetUiStateFlags()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        viewModel.uiState.value.dateTimeMillis?.let {
            calendar.timeInMillis = it
        } ?: run {
            calendar.timeInMillis =
                System.currentTimeMillis()
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val currentMillis =
                        viewModel.uiState.value.dateTimeMillis ?: System.currentTimeMillis()
                    val currentDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(currentMillis),
                        ZoneId.systemDefault()
                    )
                    set(Calendar.HOUR_OF_DAY, currentDateTime.hour)
                    set(Calendar.MINUTE, currentDateTime.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                viewModel.updateDateTime(newCalendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        viewModel.uiState.value.dateTimeMillis?.let {
            calendar.timeInMillis = it
        } ?: run {
            calendar.timeInMillis =
                System.currentTimeMillis()
        }

        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val newCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    val currentMillis =
                        viewModel.uiState.value.dateTimeMillis ?: System.currentTimeMillis()
                    val currentDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(currentMillis),
                        ZoneId.systemDefault()
                    )
                    set(Calendar.YEAR, currentDateTime.year)
                    set(
                        Calendar.MONTH,
                        currentDateTime.monthValue - 1
                    ) // Calendar'da ay 0-11 arasıdır
                    set(Calendar.DAY_OF_MONTH, currentDateTime.dayOfMonth)
                }
                viewModel.updateDateTime(newCalendar.timeInMillis)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }


}