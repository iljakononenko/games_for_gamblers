package com.example.shoppingapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_game_result.*

class game_result : AppCompatActivity() {

    var flag_won = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        var avg_value = 0

        if (intent != null)
        {
            avg_value = intent.getIntExtra("avg_reaction", 0)
        }

        if (avg_value < 380)
        {
            flag_won = true
            tv_result_greeting.text = "Congratulations!"
        }
        else
        {
            tv_result_greeting.text = "Sorry ;("
        }

        tv_result.text = "0.$avg_value"


    }

    fun after_result(view: View)
    {

        if (Static_object.buy_flag)
        {

            var next_intent: Intent
            next_intent = Intent(this, Form_activity::class.java)
            next_intent.putExtra("flag_won", flag_won)

            startActivityForResult(next_intent,1112)
        }

        else
        {
            finish()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null)
        {
            if (Static_object.buy_flag && requestCode == 1112)
            {
                val result_intent = Intent()

                result_intent.putExtra("flag_result", data.getBooleanExtra("flag_result", false))

                setResult(Activity.RESULT_OK, result_intent)
            }
        }

        finish()
    }
}