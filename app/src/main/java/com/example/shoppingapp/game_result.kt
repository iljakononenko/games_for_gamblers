package com.example.shoppingapp

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

        if (avg_value < 400)
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
        var next_intent: Intent
        if (Static_object.buy_flag)
        {
            next_intent = Intent(this, Form_activity::class.java)
        }
        else
        {
            next_intent = Intent(this, Entertainment_decide::class.java)
        }

        next_intent.putExtra("flag_won", flag_won)

        startActivity(next_intent)
    }
}