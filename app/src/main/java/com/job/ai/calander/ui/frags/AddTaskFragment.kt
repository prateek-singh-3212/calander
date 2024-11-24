package com.job.ai.calander.ui.frags

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.job.ai.calander.R
import com.job.ai.calander.databinding.FragmentAddTaskBinding
import com.job.ai.calander.databinding.FragmentDateSelectorBinding
import com.job.ai.calander.databinding.FragmentYearSelectorBinding
import com.job.ai.calander.databinding.LoadingUiBinding
import com.job.ai.calander.models.TaskData
import com.job.ai.calander.ui.views.CustomCalendarView
import com.job.ai.calander.ui.vm.CalendarTasksViewModel
import com.job.ai.calander.utils.UIState
import com.job.ai.calander.utils.showSnackBar
import com.job.ai.calander.utils.toDateFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class AddTaskFragment : Fragment() {

    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var loadingUiBinding: LoadingUiBinding
    private val viewModel by activityViewModels<CalendarTasksViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        loadingUiBinding = LoadingUiBinding.bind(binding.root)

        observers()

        binding.textView.text = "Add Task \n " + viewModel.selectedDate.value?.time?.time?.toDateFormat()

        binding.doneBtn.setOnClickListener {
            if (invalidInput()) {
                showSnackBar(binding.root, "Invalid input, add AtLeast 3 chars...")
                return@setOnClickListener
            }
            viewModel.selectedDate.value?.let {
                viewModel.addTask(TaskData.TaskDetails(
                    title = binding.editTextTitle.text.toString(),
                    description = binding.editTextDescription.text.toString(),
                    taskDateEpoch = it.time.time, // Set the time of task
                    createdOn = Calendar.getInstance().time.time // Set the time of creation of object
                ))
            } ?: showSnackBar(binding.root, "Unable to save, No date selected")
        }

        return binding.root
    }

    private fun invalidInput(): Boolean {
        return binding.editTextTitle.text.isNullOrEmpty()
                || (binding.editTextTitle.text?.length ?: 0) < 3
                || binding.editTextDescription.text.isNullOrEmpty()
                || (binding.editTextDescription.text?.length ?: 0) < 3
    }

    private fun observers() {
        lifecycleScope.launch {
            viewModel.addTaskState.collectLatest {
                when(it) {
                    is UIState.Error -> {
                        showSnackBar(binding.root, it.exception)
                    }
                    is UIState.Loading -> {
                        if (it.isLoading){
                            loadingUiBinding.loadingBg.visibility = View.VISIBLE
                            loadingUiBinding.loadingIndicator.visibility = View.VISIBLE
                            loadingUiBinding.loadingBg.setOnClickListener {  }
                        }else{
                            loadingUiBinding.loadingBg.visibility = View.GONE
                            loadingUiBinding.loadingIndicator.visibility = View.GONE
                        }
                    }
                    is UIState.Success -> {
                        showSnackBar(binding.root, "Task Saved!!")
                    }
                }
            }
        }
    }
}