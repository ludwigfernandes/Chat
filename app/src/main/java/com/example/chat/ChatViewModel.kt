package com.example.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatViewModel : ViewModel() {


    private lateinit var databaseReference: DatabaseReference
    private val _chatList = MutableLiveData<ArrayList<Chat>>()
    val chatList: LiveData<ArrayList<Chat>> get() = _chatList

    private val firebaseUser = FirebaseAuth.getInstance().currentUser


    init {
        _chatList.value = ArrayList()
    }

    fun readMessage(receiverId: String?) {
        val senderId = firebaseUser!!.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = ArrayList<Chat>()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat.receiverId.equals(receiverId) || chat.senderId.equals(
                            receiverId
                        ) && chat.receiverId.equals(senderId)
                    ) {
                        chats.add(chat)
                    }
                }
                _chatList.value = chats
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun sendMessage(receiverId: String, message: String) {
        val senderId = firebaseUser!!.uid
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