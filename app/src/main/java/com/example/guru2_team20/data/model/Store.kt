package com.example.guru2_team20.data.model

import com.google.firebase.firestore.DocumentId

data class Store(
    @DocumentId val id: String = "",

    // 위치 관련 정보
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val geohash: String = "",

    // 가게 이름
    val name: String = "",

    // 가게 형태
    val type: String = "",

    // 동반 가능
    val size: String = "",

    // 작성자 UID
    val uid: String = "",
) {
    enum class Type {
        NONE, CAFE, RESTAURANT
    }

    enum class Size {
        BIG, SMALL, BOTH
    }
}
