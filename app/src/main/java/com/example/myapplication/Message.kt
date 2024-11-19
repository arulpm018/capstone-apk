package com.example.myapplication

data class Message(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)