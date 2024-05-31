package com.example.chat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    var chatList = ArrayList<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val intent = intent
        var receiverId = intent.getStringExtra("userId")
        var name = intent.getStringExtra("name")
        var phoneNumber = intent.getStringExtra("phoneNumber")


//        binding.etMessage.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                binding.btnCamera.visibility = View.GONE
//                binding.btnSendFile.visibility = View.GONE
//            } else {
//                binding.btnCamera.visibility = View.VISIBLE
//                binding.btnSendFile.visibility = View.VISIBLE
//            }
//        }

        binding.btnSendMessage.setOnClickListener {
            val message = binding.etMessage.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(firebaseUser!!.uid, receiverId!!, message)
                binding.etMessage.setText("")
            }
        }

        binding.rvChat.layoutManager = LinearLayoutManager(this)
        readMessage(firebaseUser!!.uid, receiverId)
    }

    private fun readMessage(senderId: String, receiverId: String?) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat.receiverId.equals(receiverId) ||
                        chat.senderId.equals(receiverId) && chat.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                binding.rvChat.adapter = ChatAdapter(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference()
        val currentTime = System.currentTimeMillis()

        var hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        hashMap["time"] = currentTime.toString()
        databaseReference.child("Chats").push().setValue(hashMap)
    }
}