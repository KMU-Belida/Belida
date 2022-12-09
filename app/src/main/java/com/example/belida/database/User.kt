package com.example.belida.database

data class User(
    var userEmail : String = "",
    var userPassword : String = "",
    var userNickName : String = "",
    var userToken : String = "",
    var userLocation : String = "",
    var belidaToken : Int = 50 // 벨리다 토큰 50개 지급
)
