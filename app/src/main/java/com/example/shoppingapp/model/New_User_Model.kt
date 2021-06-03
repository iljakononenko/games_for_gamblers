package com.example.shoppingapp.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class New_User_Model (
        var email:String? = "email",
        var Products_bought:Int? = 0,
        var Products_in_cart:Int? = 0,
        var account_balance:Int? = 0
)
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "email" to email,
                "Products_bought" to Products_bought,
                "Products_in_cart" to Products_in_cart,
                "account_balance" to account_balance

        )
    }
}