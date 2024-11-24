package com.job.ai.calander.ui.frags

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.job.ai.calander.R
import com.job.ai.calander.databinding.FragmentDateSelectorBinding
import com.job.ai.calander.databinding.LoadingUiBinding
import com.job.ai.calander.ui.views.CustomCalendarView
import com.job.ai.calander.ui.vm.CalendarTasksViewModel
import com.job.ai.calander.utils.showSnackBar
import com.job.ai.calander.utils.toDateFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DateSelectorFragment : Fragment() {

    private lateinit var binding: FragmentDateSelectorBinding
    val viewModel by activityViewModels<CalendarTasksViewModel>()
    private lateinit var loadingUiBinding: LoadingUiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDateSelectorBinding.inflate(inflater, container, false)
        loadingUiBinding = LoadingUiBinding.bind(binding.root)

        // Set selected year.
        binding.customCalendarView.setOnDateSelectedListener(object : CustomCalendarView.OnDateSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onDateSelected(date: Long) {
                binding.selectedDate.text = date.toDateFormat()
            }
        })

        binding.customCalendarView.setSelectedYear(viewModel.selectedMonth.value, viewModel.selectedYear.value)
        viewModel.selectedDate.value?.let {
            binding.customCalendarView.setSelectedDate(it)
        }

        binding.addTask.setOnClickListener {
            binding.customCalendarView.dateSelected?.let {
                viewModel.selectedDate.value = it
                findNavController().navigate(R.id.action_date_fragment_to_add_task_fragment)
            } ?: showSnackBar(binding.root, "Select the date.")
        }

        binding.viewTask.setOnClickListener {
            binding.customCalendarView.dateSelected?.let {
                viewModel.selectedDate.value = it
                findNavController().navigate(R.id.action_date_fragment_to_task_list_fragment)
            } ?: showSnackBar(binding.root, "Select the date.")
        }
        return binding.root
    }
}