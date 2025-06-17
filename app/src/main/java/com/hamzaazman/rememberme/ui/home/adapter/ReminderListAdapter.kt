package com.hamzaazman.rememberme.ui.home.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hamzaazman.rememberme.R
import com.hamzaazman.rememberme.data.model.Reminder
import com.hamzaazman.rememberme.data.model.ReminderPriority
import com.hamzaazman.rememberme.databinding.ItemReminderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ReminderListAdapter(
    private val onItemClicked: (Reminder) -> Unit,
    private val onCompletedChanged: (Reminder, Boolean) -> Unit
) : ListAdapter<Reminder, ReminderListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReminderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.checkboxCompleted.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val reminder = getItem(position)
                    onCompletedChanged(reminder, isChecked)
                }
            }

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(position))
                }
            }
        }

        fun bind(reminder: Reminder) {
            binding.apply {
                textViewReminderTitle.text = reminder.title

                if (!reminder.description.isNullOrBlank()) {
                    textViewReminderDescription.text = reminder.description
                    textViewReminderDescription.visibility = View.VISIBLE
                } else {
                    textViewReminderDescription.visibility = View.GONE
                }

                val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                textViewReminderDatetime.text = dateFormat.format(Date(reminder.dateTime))

                if (reminder.isCompleted) {
                    textViewReminderTitle.paintFlags =
                        textViewReminderTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    textViewReminderDescription.paintFlags =
                        textViewReminderDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    textViewReminderDatetime.paintFlags =
                        textViewReminderDatetime.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    textViewReminderTitle.paintFlags =
                        textViewReminderTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    textViewReminderDescription.paintFlags =
                        textViewReminderDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    textViewReminderDatetime.paintFlags =
                        textViewReminderDatetime.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                checkboxCompleted.isChecked = reminder.isCompleted


                val priorityColorResId = when (reminder.priority) {
                    ReminderPriority.LOW -> R.drawable.shape_priority_indicator_low
                    ReminderPriority.MEDIUM -> R.drawable.shape_priority_indicator_medium
                    ReminderPriority.HIGH -> R.drawable.shape_priority_indicator_high
                }
                viewPriorityIndicator.setBackgroundResource(priorityColorResId)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) =
            oldItem == newItem
    }
}