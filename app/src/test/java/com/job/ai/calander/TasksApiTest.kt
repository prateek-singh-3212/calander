package com.job.ai.calander

import com.job.ai.calander.models.SuccessResponseData
import com.job.ai.calander.models.TasksListData
import com.job.ai.calander.models.StoreTaskData
import com.job.ai.calander.network.apis.TasksApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.job.ai.calander.models.TaskData
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.*

class TasksApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var tasksApi: TasksApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val gson: Gson = GsonBuilder().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        tasksApi = retrofit.create(TasksApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test storeTask success`() = runBlocking {
        val storeTaskData = StoreTaskData(
            userId = 111,
            taskDetail = TaskData.TaskDetails(
                title = "dasd",
                description = "gdfgd",
                taskDateEpoch = 1L,
                createdOn = 1L
            )
        )

        mockWebServer.enqueue(MockResponse()
            .setBody("{\"status\":\"Success\"}")
            .setResponseCode(200)
        )

        val response: Response<SuccessResponseData> = tasksApi.storeTask(storeTaskData)

        assert(response.isSuccessful)
        assertEquals("Success", response.body()?.status)
    }

    @Test
    fun `test deleteTask success`() = runBlocking {
        val userId = 1
        val taskId = 101

        mockWebServer.enqueue(MockResponse()
            .setBody("{\"status\":\"Success\"}")
            .setResponseCode(200)
        )

        val response: Response<SuccessResponseData> = tasksApi.deleteTask(userId, taskId)

        assert(response.isSuccessful)
        assertEquals("Success", response.body()?.status)
    }

    @Test
    fun `test getTasks success`() = runBlocking {
        // Given
        val userId = 1
        val tasksListData = TasksListData(
            tasksList = listOf(
                TaskData(
                    taskId = 1,
                    taskDetail =  TaskData.TaskDetails(
                        title = "dasd",
                        description = "gdfgd",
                        taskDateEpoch = 1L,
                        createdOn = 1L
                    )
                ),
                TaskData(
                    taskId = 2,
                    taskDetail =  TaskData.TaskDetails(
                        title = "dasd",
                        description = "gdfgd",
                        taskDateEpoch = 1L,
                        createdOn = 1L
                    )
                )
            )
        )

        mockWebServer.enqueue(MockResponse()
            .setBody(Gson().toJson(tasksListData)) // Assuming an empty task list is returned for simplicity
            .setResponseCode(200)
        )

        val response: Response<TasksListData> = tasksApi.getTasks(userId)

        assert(response.isSuccessful)
        assertEquals(2, response.body()?.tasksList?.size)
    }

    @Test
    fun `test storeTask failure`() = runBlocking {
        val storeTaskData = StoreTaskData(
            userId = 111,
            taskDetail = TaskData.TaskDetails(
                title = "dasd",
                description = "gdfgd",
                taskDateEpoch = 1L,
                createdOn = 1L
            )
        )

        mockWebServer.enqueue(MockResponse()
            .setBody("{\"status\":\"error\"}")
            .setResponseCode(500)
        )

        val response: Response<SuccessResponseData> = tasksApi.storeTask(storeTaskData)

        assert(response.code() == 500)
    }

    @Test
    fun `test getTasks failure`() = runBlocking {
        val userId = 1

        mockWebServer.enqueue(MockResponse()
            .setBody("{\"status\":\"error\"}")
            .setResponseCode(500)
        )

        val response: Response<TasksListData> = tasksApi.getTasks(userId)

        assert(response.code() == 500)
    }
}
