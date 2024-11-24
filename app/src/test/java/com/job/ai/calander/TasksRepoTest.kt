package com.job.ai.calander

import com.job.ai.calander.models.*
import com.job.ai.calander.network.apis.TasksApi
import com.job.ai.calander.network.repo.TasksRepo
import com.job.ai.calander.utils.ApiResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.*
import retrofit2.Response

@ExperimentalCoroutinesApi
class TasksRepoTest {

    @Mock
    lateinit var tasksApi: TasksApi

    lateinit var tasksRepo: TasksRepo

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Initialize mocks
        tasksRepo = TasksRepo(tasksApi)
    }

    @Test
    fun `test storeTask success`() = runTest {
        val taskDetails = TaskData.TaskDetails(
            title = "Task 1",
            description = "Description of Task 1",
            taskDateEpoch = 1625074800000L,
            createdOn = 1625074800000L
        )
        val storeTaskData = StoreTaskData(userId = 888, taskDetail = taskDetails)

        val successResponse = SuccessResponseData(status = "Success")
        Mockito.`when`(tasksApi.storeTask(storeTaskData)).thenReturn(Response.success(successResponse))

        val result = tasksRepo.storeTask(taskDetails)

        result.collectLatest {
            when(it) {
                is ApiResult.Failure -> {
                    fail("invalid response")
                }
                is ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    assertEquals("Success", it.data.status)
                }
            }
        }
    }

    @Test
    fun `test storeTask failure`() = runTest {
        val taskDetails = TaskData.TaskDetails(
            title = "Task 2",
            description = "Description of Task 2",
            taskDateEpoch = 1625074800000L,
            createdOn = 1625074800000L
        )
        val storeTaskData = StoreTaskData(userId = 888, taskDetail = taskDetails)

        Mockito.`when`(tasksApi.storeTask(storeTaskData)).thenReturn(Response.error(500, okhttp3.ResponseBody.create(null, "")))

        val result = tasksRepo.storeTask(taskDetails)

        result.collectLatest {
            when(it) {
                is ApiResult.Failure -> {
                    assertFalse(it.exception.message.isNullOrEmpty())
                }
                is ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    fail("invalid response")
                }
            }
        }
    }

    @Test
    fun `test deleteTask success`() = runTest {
        val taskId = 123
        val successResponse = SuccessResponseData(status = "Success")

        Mockito.`when`(tasksApi.deleteTask(888, taskId)).thenReturn(Response.success(successResponse))

        val result = tasksRepo.deleteTask(taskId)

        result.collectLatest {
            when(it) {
                is ApiResult.Failure -> {
                    fail("invalid response")
                }
                is ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    assertEquals("Success", it.data.status)
                }
            }
        }
    }

    @Test
    fun `test deleteTask failure`() = runTest {
        val taskId = 123

        Mockito.`when`(tasksApi.deleteTask(888, taskId)).thenReturn(Response.error(500, okhttp3.ResponseBody.create(null, "")))

        val result = tasksRepo.deleteTask(taskId)

        result.collectLatest {
            when(it) {
                is ApiResult.Failure -> {
                    assertFalse(it.exception.message.isNullOrEmpty())
                }
                is ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    fail("invalid response")
                }
            }
        }
    }

    @Test
    fun `test getTasks success`() = runTest {
        val tasksListData = TasksListData(
            tasksList = listOf(
                TaskData(
                    taskId = 1,
                    taskDetail = TaskData.TaskDetails(
                        title = "Task 1",
                        description = "Description of Task 1",
                        taskDateEpoch = 1625074800000L,
                        createdOn = 1625074800000L
                    )
                )
            )
        )

        Mockito.`when`(tasksApi.getTasks(888)).thenReturn(Response.success(tasksListData))

        val result = tasksRepo.listAllTasks()

        result.collectLatest {
            when(it) {
                is ApiResult.Failure -> {
                    fail("invalid response")
                }
                is ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    assertEquals(1, it.data.tasksList.size)
                }
            }
        }
    }

    @Test
    fun `test getTasks failure`() = runTest {
        Mockito.`when`(tasksApi.getTasks(888)).thenReturn(Response.error(500, okhttp3.ResponseBody.create(null, "")))

        val result = tasksRepo.listAllTasks()

        result.collectLatest {
            when(it) {
                is ApiResult.Failure -> {
                    assertFalse(it.exception.message.isNullOrEmpty())
                }
                is ApiResult.Loading -> {
                }
                is ApiResult.Success -> {
                    fail("invalid response")
                }
            }
        }
    }
}
