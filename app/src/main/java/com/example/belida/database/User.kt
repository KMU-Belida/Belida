package com.example.belida.database

data class User(
    val userEmail : String = "",
    val userPassword : String = "",
    val userName : String = "",
    val userNickName : String = "",
    val userToken : String = "",
    var userLocation : String = ""
)
