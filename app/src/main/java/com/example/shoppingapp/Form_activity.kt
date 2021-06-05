package com.example.shoppingapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_form_activity.*
import kotlinx.android.synthetic.main.activity_game.*

class Form_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_activity)

        iv_purchase_success.visibility = View.INVISIBLE
        tv_thanks.visibility = View.INVISIBLE

    }

    fun submit_purchase(view: View)
    {

        if (intent != null && intent.getBooleanExtra("flag_won", false) )
        {
            Static_object.user_discount = true
        }

        change_layout()

        val handler = Handler()
        handler.postDelayed( {

            Static_object.products_bought = true

            val result_intent = Intent()

            result_intent.putExtra("flag_result",true)

            setResult(Activity.RESULT_OK, result_intent)
            finish()

        }, 5000)

    }

    fun change_layout()
    {
        iv_purchase_success.visibility = View.VISIBLE
        tv_thanks.visibility = View.VISIBLE

        btn_submit_purchase.visibility = View.INVISIBLE
        ed_city.visibility = View.INVISIBLE
        ed_country.visibility = View.INVISIBLE
        ed_first_name.visibility = View.INVISIBLE
        ed_last_name.visibility = View.INVISIBLE
        ed_phone.visibility = View.INVISIBLE
        ed_postal_code.visibility = View.INVISIBLE
        ed_region.visibility = View.INVISIBLE
        ed_street_address.visibility = View.INVISIBLE


        ed_country.isEnabled = false
        ed_first_name.isEnabled = false
        ed_last_name.isEnabled = false
        ed_phone.isEnabled = false
        ed_postal_code.isEnabled = false
        ed_region.isEnabled = false
        ed_street_address.isEnabled = false

    }

    override fun onBackPressed() {

        val result_intent = Intent()
        setResult(Activity.RESULT_CANCELED, result_intent)
        super.onBackPressed()

    }


}