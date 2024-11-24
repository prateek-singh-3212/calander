package com.job.ai.calander.models

import com.google.gson.annotations.SerializedName

data class TasksListData(
    @SerializedName("tasks")
    val tasksList: List<TaskData>
)