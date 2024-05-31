package com.example.chat

data class Chat(
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var time: String = ""
)
