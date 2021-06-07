package com.example.shoppingapp.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class New_User_product_Model (
        var product_id:Int? = -1,
        var product_amount:Int? = 1
)
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "product_id" to product_id,
                "product_amount" to product_amount
        )
    }
}