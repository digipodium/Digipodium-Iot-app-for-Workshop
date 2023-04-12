package com.example.iotworkshop

data class DataItem(
    val device: String = "raspberry pi",
    val humidity: Int = 0,
    val temperature: Int = 0,
    val timestamp: Double = 0.0
)
