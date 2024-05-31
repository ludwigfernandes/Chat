package com.example.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private val chatList: ArrayList<Chat>
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MESSAGE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (chatList[position].senderId.equals(currentUser!!.uid)) {
            return MESSAGE_RIGHT
        } else {
            return MESSAGE_LEFT
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        val messageTime =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(chat.time.toLong()))
        holder.tvMessage.text = chat.message
        holder.tvTimestamp.text = messageTime
    }
}