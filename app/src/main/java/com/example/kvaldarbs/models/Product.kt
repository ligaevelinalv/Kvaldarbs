package com.example.kvaldarbs.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
//product data class, used to recieve and publish product data to and from the database
data class Product(
    //required attributes
    var title: String = "",
    var type: String = "",
    var manufacturer: String = "",
    var delivery: String = "",
    var wear: String = "",
    var amount: Int = 1,
    var location: String = "",
    var description: String = "",
    var isordered: Boolean? = false,
    var key: String? = "",
    //type specific attributes
    //furniture type attributes
    var weight: Int? = 0,
    var height: Int? = 0,
    var width: Int? = 0,
    var length: Int? = 0,
    //furniture and decoration type attributes
    var material: String? = "",
    var color: String? = "",
    //book type attributes
    var author: String? = "",
    var year: Int? = 0,
    var book_title: String? = "",
    //decoration type attributes
    var size: String? = "",
    //organizational atributes for visibility
    var visible: Boolean? = false,
    var admincritic: String? = ""

) {


    @Exclude
    //creates hashmap that cap be pushed to the database
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "title" to title,
                "type" to type,
                "manufacturer" to manufacturer,
                "delivery" to delivery,
                "wear" to wear,
                "amount" to amount,
                "location" to location,
                "description" to description,
                "key" to key,
                "isordered" to isordered,
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
                "size" to size,
                //admin
                "visible" to visible,
                "admincritic" to admincritic,
        )
    }


}
















