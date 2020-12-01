package com.example.kvaldarbs.models

import com.google.firebase.database.Exclude

data class Offer(
        var id: String = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "key" to id
        )
    }


}