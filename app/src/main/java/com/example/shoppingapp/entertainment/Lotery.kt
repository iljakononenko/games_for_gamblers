package com.example.shoppingapp.entertainment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.R
import kotlinx.android.synthetic.main.activity_lotery.*
import kotlin.random.Random

class Lotery : AppCompatActivity(), Lotery_recycler_adapter.my_OnItemClickListener {

    var button_pressed_counter = 0
    var arraylist_result = ArrayList<Int>()
    var numbers = ArrayList<Int>()

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

        if (button_pressed_counter < 2)
        {
            arraylist_result.add(numbers.get(position))
            button_pressed_counter++
        }
        else
        {
            var tv_lotery_result = findViewById<TextView>(R.id.tv_lotery_result)

            if (arraylist_result.get(0) == arraylist_result.get(1) && arraylist_result.get(0) == arraylist_result.get(2))
            {

                tv_lotery_result.text = "You won! :)"
                tv_lotery_result.textSize = 24f
            }
            else
            {
                tv_lotery_result.text = "You lost ;("
                tv_lotery_result.textSize = 24f
            }

        }


    }
}