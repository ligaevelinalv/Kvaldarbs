package com.example.kvaldarbs.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(
    var title: String = "",
    var type: String? = "",
    var manufacturer: String = "",
    var weight: Int? = 0,
    var delivery: String = "",
    var wear: String? = "",
    var amount: Int? = 0,
    //var location: String = "",
    var description: String? = "",
    var isordered: Boolean? = false,
    var key: String? = ""
) {


    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "title" to title,
                "type" to type,
                "manufacturer" to manufacturer,
                "weight" to weight,
                "delivery" to delivery,
                "wear" to wear,
                "amount" to amount,
                "description" to description,
                "key" to key,
                "isordered" to isordered
        )
    }


}



