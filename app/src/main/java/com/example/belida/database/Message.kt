package com.example.belida.database

data class Message(
    var message: String = "",
    var sendEmail: String = "",
    var type: Int = 1,
    var isViewed: Boolean = true,
    val reservationToken: String = "10",
    val depositToken: String = "10"
)
