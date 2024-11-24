package com.job.ai.calander.ui.frags

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.job.ai.calander.databinding.FragmentTaskListBinding
import com.job.ai.calander.databinding.LoadingUiBinding
import com.job.ai.calander.models.TaskData
import com.job.ai.calander.ui.adapters.TaskAdapter
import com.job.ai.calander.ui.vm.CalendarTasksViewModel
import com.job.ai.calander.utils.UIState
import com.job.ai.calander.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TaskListFragment : Fragment() {

    private lateinit var binding: FragmentTaskListBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var loadingUiBinding: LoadingUiBinding
    private val viewModel by activityViewModels<CalendarTasksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        loadingUiBinding = LoadingUiBinding.bind(binding.root)

        observers()

        viewModel.getAllTasks()
        taskAdapter = TaskAdapter(
            onTaskDelete = { taskId ->
                viewModel.deleteTask(taskId)
            }
        )

        binding.tasksRv.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        return binding.root
    }

    private fun observers() {
        lifecycleScope.launch {
            viewModel.taskListState.collectLatest {
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
                        viewModel.selectedDate.value?.let { date ->
                            val filterList = getTasksForSelectedDate(
                                date.clone() as Calendar,
                                it.data
                            )
                            if (filterList.isEmpty())
                                showSnackBar(binding.root, "No Data found")
                            taskAdapter.setData(filterList)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.deleteTask.collectLatest {
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
                        showSnackBar(binding.root, "Task Deleted, Syncing task list with server...")
                    }
                }
            }
        }
    }

    private fun getTasksForSelectedDate(
        selectedDate: Calendar,
        taskList: List<TaskData>
    ): List<TaskData> {
        // Convert the selected calendar date to epoch milliseconds (start of the day)
        val selectedDateStartOfDay = selectedDate.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val selectedDateEndOfDay = selectedDate.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        // Filter tasks based on the selected date (taskDateEpoch should be within the same day)
        return taskList.filter { task ->
            task.taskDetail.taskDateEpoch in selectedDateStartOfDay..selectedDateEndOfDay
        }
    }
}