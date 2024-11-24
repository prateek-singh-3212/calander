package com.job.ai.calander.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.job.ai.calander.databinding.CardTaskBinding
import com.job.ai.calander.models.TaskData
import com.job.ai.calander.utils.toDateFormat

class TaskAdapter(val onTaskDelete: (taskId: Int) -> Unit) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val taskList: MutableList<TaskData> = mutableListOf()

    class TaskViewHolder(val binding: CardTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            CardTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.taskTitle.text = task.taskDetail.title
        holder.binding.taskDescription.text = task.taskDetail.description
        holder.binding.taskCreatedOn.text = "Created on: ${task.taskDetail.createdOn.toDateFormat()}"
        holder.binding.taskDate.text = "Created on: ${task.taskDetail.taskDateEpoch.toDateFormat()}"
        holder.binding.taskId.text = "Task ID: ${task.taskId}"

        holder.binding.deleteTask.setOnClickListener {
            onTaskDelete(task.taskId)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setData(newData: List<TaskData>) {
        val prevSize = taskList.size
        if (prevSize > newData.size) {
            // Item deleted
            taskList.removeAt(prevSize-1)
            notifyItemRemoved(prevSize-1)
            taskList.clear()
            taskList.addAll(newData)
            notifyItemRangeChanged(0, newData.size)
        }else {
            // Item added
            taskList.addAll(newData)
            notifyItemRangeChanged(0, newData.size)
        }
    }
}
