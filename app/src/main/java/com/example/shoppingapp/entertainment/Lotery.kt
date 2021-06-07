package com.example.shoppingapp.entertainment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.Entertainment_decide
import com.example.shoppingapp.Form_activity
import com.example.shoppingapp.R
import com.example.shoppingapp.Static_object
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_lotery.*
import kotlin.random.Random

class Lotery : AppCompatActivity(), Lotery_recycler_adapter.my_OnItemClickListener {

    var button_pressed_counter = 0
    var arraylist_result = ArrayList<Int>()
    var numbers = ArrayList<Int>()

    var flag_won = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lotery)

        var tv_lotery_result = findViewById<TextView>(R.id.tv_lotery_result)

        tv_lotery_result.textSize = 0f

        for (i in 1..9)
        {
            numbers.add(Random.nextInt(1,4))
        }

        recycler_lotery.layoutManager = GridLayoutManager(this, 3)
        recycler_lotery.adapter = Lotery_recycler_adapter(numbers, this)


    }

    override fun check_lotery_result(position: Int) {

        arraylist_result.add( numbers.get(position) )
        button_pressed_counter++

        if (button_pressed_counter >= 3)
        {
            //Log.d("Test", "${arraylist_result[0]}, ${arraylist_result[1]}, ${arraylist_result[2]}")

            var tv_lotery_result = findViewById<TextView>(R.id.tv_lotery_result)

            if (arraylist_result[0] == arraylist_result[1] && arraylist_result[1] == arraylist_result[2])
            {

                tv_lotery_result.text = "You won! :)"
                tv_lotery_result.textSize = 24f

                flag_won = true
            }
            else
            {
                tv_lotery_result.text = "You lost ;("
                tv_lotery_result.textSize = 24f
            }

            val handler = Handler()
            handler.postDelayed( {

                if (Static_object.buy_flag)
                {
                    var next_intent: Intent
                    next_intent = Intent(this, Form_activity::class.java)
                    next_intent.putExtra("flag_won", flag_won)

                    startActivityForResult(next_intent, 12345)
                }

                else
                {
                    finish()
                }

            }, 5000)


        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null)
        {
            if (Static_object.buy_flag && requestCode == 12345)
            {
                val result_intent = Intent()

                result_intent.putExtra("flag_result", data.getBooleanExtra("flag_result", false))

                setResult(Activity.RESULT_OK, result_intent)
            }
        }

        finish()
    }
}