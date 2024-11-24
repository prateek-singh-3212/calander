package com.job.ai.calander.ui.frags

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.job.ai.calander.R
import com.job.ai.calander.databinding.FragmentYearSelectorBinding
import com.job.ai.calander.ui.vm.CalendarTasksViewModel
import com.job.ai.calander.utils.toDateFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class YearSelectorFragment : Fragment() {

    private lateinit var binding: FragmentYearSelectorBinding
    private val viewModel by activityViewModels<CalendarTasksViewModel>()
    private val yearList = (1980..2050).toList()
    private val monthsList = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYearSelectorBinding.inflate(inflater, container, false)

        binding.year.apply {
            setText(viewModel.selectedYear.value.toString(), false)
            setOnClickListener {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, yearList)
                setAdapter(adapter)
                showDropDown()
            }
            setOnItemClickListener { parent, view, position, id ->
                viewModel.selectedYear.value = yearList[position]
            }
        }

        binding.month.apply {
            setText(monthsList[viewModel.selectedMonth.value], false)
            setOnClickListener {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, monthsList)
                setAdapter(adapter)
                showDropDown()
            }
            setOnItemClickListener { parent, view, position, id ->
                viewModel.selectedMonth.value = position
            }
        }

        binding.nextBtn.setOnClickListener {
            viewModel.selectedDate.value = null
            findNavController().navigate(R.id.action_year_fragment_to_date_fragment)
        }

        return binding.root
    }
}