package com.example.kvaldarbs.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class EditProduct(
    var title: String = "",
    var manufacturer: String = "",
    var delivery: String = "",
    var amount: Int = 1,
    var location: String = "",
    var description: String = "",
    //mēbeles
    var weight: Int?,
    var height: Int?,
    var width: Int?,
    var length: Int?,
    //mēbeles un dekori
    var material: String?,
    var color: String?,
    //books
    var author: String?,
    var year: Int?,
    var book_title: String?,
    //dekori
    var size: String?,

) {


    @Exclude
    fun toMap(type: String): Map<String, Any?> {

        if (type == "Book") {
            return mapOf(
                    "title" to title,
                    "manufacturer" to manufacturer,
                    "delivery" to delivery,
                    "amount" to amount,
                    "location" to location,
                    "description" to description,
                    //mēbeles
                    "weight" to weight,
                    "height" to height,
                    "width" to width,
                    "length" to length,
                    //mēbeles un dekori
                    "material" to material,
                    "color" to color,
                    //books
                    "author" to author,
                    "year" to year,
                    "book_title" to book_title,
                    //dekori
                    "size" to size
            )
        }

        return mapOf(
                "title" to title,
                "manufacturer" to manufacturer,
                "delivery" to delivery,
                "amount" to amount,
                "location" to location,
                "description" to description,
                //mēbeles
                "weight" to weight,
                "height" to height,
                "width" to width,
                "length" to length,
                //mēbeles un dekori
                "material" to material,
                "color" to color,
                //books
                "author" to author,
                "year" to year,
                "book_title" to book_title,
                //dekori
                "size" to size
        )
    }


}
















