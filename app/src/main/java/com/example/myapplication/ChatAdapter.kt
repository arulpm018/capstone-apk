package com.example.myapplication

import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter : ListAdapter<Message, ChatAdapter.ChatViewHolder>(MessageDiffCallback()) {

    private val messages = mutableListOf<Pair<Spannable, Boolean>>()  // Use Spannable instead of String

    fun addMessage(message: Spannable, isUser: Boolean) {  // Accept Spannable
        messages.add(Pair(message, isUser))
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_bubble, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val (message, isUser) = messages[position]
        holder.bind(message, isUser)
    }

    override fun getItemCount(): Int = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        fun bind(message: Spannable, isUser: Boolean) {
            messageText.text = message  // Set Spannable directly
            val background = if (isUser) {
                R.drawable.bubble_background // User message bubble
            } else {
                R.drawable.bubble_background_bot // Bot message bubble
            }
            messageText.setBackgroundResource(background)
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message) =
            oldItem.timestamp == newItem.timestamp

        override fun areContentsTheSame(oldItem: Message, newItem: Message) =
            oldItem == newItem
    }
}
