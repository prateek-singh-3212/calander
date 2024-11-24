package com.job.ai.calander.models

import com.google.gson.annotations.SerializedName

data class TaskData(
    @SerializedName("task_id")
    val taskId: Int,
    @SerializedName("task_detail")
    val taskDetail: TaskDetails
) {
    data class TaskDetails(
        val title: String,
        val description: String,
        val taskDateEpoch: Long,
        val createdOn: Long
    )
}
