package com.job.ai.calander.network.repo

import com.job.ai.calander.models.SuccessResponseData
import com.job.ai.calander.models.TaskData
import com.job.ai.calander.models.TasksListData
import com.job.ai.calander.network.apis.TasksApi
import com.job.ai.calander.models.StoreTaskData
import com.job.ai.calander.utils.ApiResult
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class TasksRepo @Inject constructor(
    private val authenticationApi: TasksApi
) {

    suspend fun storeTask(taskDetails: TaskData.TaskDetails) =
        ApiResult<SuccessResponseData>(Dispatchers.IO) {
            authenticationApi.storeTask(
                StoreTaskData(
                    888,
                    taskDetails
                )
            )
        }

    suspend fun deleteTask(taskId: Int) = ApiResult<SuccessResponseData>(Dispatchers.IO) {
        authenticationApi.deleteTask(888, taskId)
    }

    suspend fun listAllTasks() = ApiResult<TasksListData>(Dispatchers.IO) {
        authenticationApi.getTasks(888)
    }
}