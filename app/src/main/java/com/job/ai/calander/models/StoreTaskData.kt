package com.job.ai.calander.models

import com.google.gson.annotations.SerializedName

data class StoreTaskData(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("task")
    val taskDetail: TaskData.TaskDetails
)