package com.google.firebase.quickstart.database.kotlin.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

// [START blog_user_class]
@IgnoreExtraProperties
data class User(
    var email: String = "",
    var phone: Int = 0,
    var role: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "email" to email,
                "phone" to phone,
                "role" to role
        )
    }
}

