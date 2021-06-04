package com.example.shoppingapp

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Static_object {
    companion object {
        var static_field = "static field"

        lateinit var preferences:SharedPreferences

        lateinit var user_id: String

        lateinit var ref_products_in_cart: DatabaseReference
        lateinit var ref_products_bought: DatabaseReference
        lateinit var ref_current_user: DatabaseReference

        fun update_data()
        {
            user_id = preferences.getInt("user_id", -1).toString()

            ref_products_in_cart = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user_id)
                    .child("Products_in_cart")

            ref_products_bought = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user_id)
                    .child("Products_bought")

            ref_current_user = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user_id)
        }
    }
}