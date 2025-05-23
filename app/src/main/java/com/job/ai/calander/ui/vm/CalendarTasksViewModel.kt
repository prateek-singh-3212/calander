package com.job.ai.calander.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.job.ai.calander.models.TaskData
import com.job.ai.calander.network.repo.TasksRepo
import com.job.ai.calander.utils.ApiResult
import com.job.ai.calander.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarTasksViewModel @Inject constructor(
    private val tasksRepo: TasksRepo
) : ViewModel() {

    private val _addTaskState: MutableStateFlow<UIState<Boolean>> =
        MutableStateFlow(UIState.loading(false))
    val addTaskState = _addTaskState.asStateFlow()

    private val _taskListState: MutableStateFlow<UIState<List<TaskData>>> =
        MutableStateFlow(UIState.loading(false))
    val taskListState = _taskListState.asStateFlow()

    private val _deleteTask: MutableStateFlow<UIState<Boolean>> =
        MutableStateFlow(UIState.loading(false))
    val deleteTask = _deleteTask.asStateFlow()

    val selectedDate: MutableStateFlow<Calendar?> = MutableStateFlow(null)

    val selectedYear: MutableStateFlow<Int> = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))

    val selectedMonth: MutableStateFlow<Int> = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))

    fun getAllTasks() {
        viewModelScope.launch {
            viewModelScope.launch {
                tasksRepo.listAllTasks().collectLatest {
                    when (it) {
                        is ApiResult.Failure -> {
                            _taskListState.emit(
                                UIState.error(it.exception.toString())
                            )
                        }

                        is ApiResult.Loading -> {
                            _taskListState.emit(
                                UIState.loading(it.progress)
                            )
                        }

                        is ApiResult.Success -> {
                            _taskListState.emit(
                                UIState.success(
                                    it.data.tasksList
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun addTask(taskDetails: TaskData.TaskDetails) {
        viewModelScope.launch(start = CoroutineStart.LAZY) {
            tasksRepo.storeTask(taskDetails).collectLatest {
                when (it) {
                    is ApiResult.Failure -> {
                        _addTaskState.emit(
                            UIState.error(it.exception.toString())
                        )
                    }

                    is ApiResult.Loading -> {
                        _addTaskState.emit(
                            UIState.loading(it.progress)
                        )
                    }

                    is ApiResult.Success -> {
                        if (it.data.status == "Success") {
                            _addTaskState.emit(
                                UIState.success(true)
                            )
                        } else {
                            _addTaskState.emit(
                                UIState.error("unable to get success response from server: ${it.data.status}")
                            )
                        }
                    }
                }
            }
        }
    }


    fun deleteTask(taskID: Int) {
        viewModelScope.launch {
            viewModelScope.launch {
                tasksRepo.deleteTask(taskID).collectLatest {
                    when (it) {
                        is ApiResult.Failure -> {
                            _deleteTask.emit(
                                UIState.error(it.exception.toString())
                            )
                        }

                        is ApiResult.Loading -> {
                            _deleteTask.emit(
                                UIState.loading(it.progress)
                            )
                        }

                        is ApiResult.Success -> {
                            if (it.data.status == "Success") {
                                _deleteTask.emit(
                                    UIState.success(true)
                                )
                                // Sync the current task list with new updated task list from server. for consistency
                                getAllTasks()
                            } else {
                                _addTaskState.emit(
                                    UIState.error("unable to get success response from server: ${it.data.status}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}