package com.example.belida.model

import java.sql.Timestamp
//val storeItemList = arrayListOf<ContentDTO>()

data class ContentDTO(var explain : String? = null,
                      var imageUrl : String? = null,
                      var itemname : String? = null,
                      var category : String? = null,
                      var price : String? = null,
    //누가 올렸는지 확인하기 위한 uid (email값 아님!)
                      var uid : String? = null,
    // 올린 유저의 이미지 관리를 위한 변수 userId (email값임)
                      var userId : String? = null,
    // 언제 올렸는지 알기 위한 변수 timestamp
                      var timestamp : Long? = null,
                      var favoriteCount : Int = 0,
                      var favorites : Map<String, Boolean> = HashMap()){

    data class Comment(var uid : String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp: Long? = null)
}