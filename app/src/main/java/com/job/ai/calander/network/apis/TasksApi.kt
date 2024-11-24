package com.job.ai.calander.network.apis

import com.job.ai.calander.models.SuccessResponseData
import com.job.ai.calander.models.TasksListData
import com.job.ai.calander.models.StoreTaskData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TasksApi {

    @POST("api/storeCalendarTask")
    suspend fun storeTask(
        @Body task: StoreTaskData
    ): Response<SuccessResponseData>

    @FormUrlEncoded
    @POST("api/deleteCalendarTask")
    suspend fun deleteTask(
        @Field("user_id") userId: Int,
        @Field("task_id") taskId: Int
    ): Response<SuccessResponseData>

    @FormUrlEncoded
    @POST("api/getCalendarTaskList")
    suspend fun getTasks(
        @Field("user_id") userId: Int
    ): Response<TasksListData>
}