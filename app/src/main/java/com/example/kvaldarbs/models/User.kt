package com.google.firebase.quickstart.database.kotlin.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

//user data class, used to recieve and publish user data to and from the database
@IgnoreExtraProperties
data class User(
    var email: String = "",
    var phone: Int = 0,
    var role: String = ""
) {
    @Exclude
    //creates hashmap that cap be pushed to the database
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "email" to email,
                "phone" to phone,
                "role" to role
        )
    }
}

