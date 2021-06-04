package com.example.shoppingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Form_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_activity)

        Static_object.buy_flag = false
    }
}