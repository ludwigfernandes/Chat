package com.example.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        val intent = intent
        var receiverId = intent.getStringExtra("userId")
        var name = intent.getStringExtra("name")
        var phoneNumber = intent.getStringExtra("phoneNumber")


        binding.btnSendMessage.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sendMessage(receiverId!!, message)
                binding.etMessage.setText("")
            }
        }

        binding.rvChat.layoutManager = LinearLayoutManager(this)
        viewModel.chatList.observe(this, Observer { chats ->
            binding.rvChat.adapter = ChatAdapter(chats)
        })
        viewModel.readMessage(receiverId)
    }
}