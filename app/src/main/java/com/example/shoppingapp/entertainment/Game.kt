package com.example.shoppingapp.entertainment

import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppingapp.R
import com.example.shoppingapp.game_result
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.random.Random

class Game : AppCompatActivity() {

    var start_time : Long = 0
    var time_millis : Long = 0
    var flag_game_started = false
    var flag_color_changed = false
    var flag_penalty = false
    var index_of_attempts = 1
    var reaction_results:Array<Long> = arrayOf(0, 0, 0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        tv_to_click.setOnClickListener{ tv_to_click_clicked() }

        btn_next_try.setBackgroundColor(Color.BLUE)

        btn_see_result.isClickable = false
        btn_see_result.visibility = View.INVISIBLE

        start_attempt()

    }

    fun next_attempt(v: View)
    {
        index_of_attempts++
        tv_attempts.text = "Attempt $index_of_attempts/5"
        start_attempt()
    }

    fun tv_to_click_clicked ()
    {

        Log.d("Test", "View was clicked!")

        if (flag_game_started && flag_color_changed)
        {
            time_millis = (System.nanoTime() - start_time) / 1000000

            if (!flag_penalty)
            {
                reaction_results[index_of_attempts-1] = time_millis
                Log.d("Test", "time in millis: $time_millis; time in array: ${reaction_results[index_of_attempts-1]}")
                flag_penalty = false
            }

            tv_to_click.setBackgroundColor(Color.BLACK)
            tv_to_click.text = "0.$time_millis seconds"
            tv_to_click.isClickable = false

            if (index_of_attempts < 5)
            {
                btn_next_try.isClickable = true
                btn_next_try.visibility = View.VISIBLE
            }
            else
            {
                btn_see_result.isClickable = true
                btn_see_result.visibility = View.VISIBLE
            }

            btn_reset_attempts.isClickable = true
            btn_reset_attempts.visibility = View.VISIBLE

            flag_game_started = false
            flag_color_changed = false
        }

        else if (flag_game_started)
        {
            reaction_results[index_of_attempts-1] = 1000
            flag_penalty = true
            Toast.makeText(this, "Too early! Penalty 1s!", Toast.LENGTH_SHORT).show()
        }

    }

    fun reset_attempts (v : View)
    {
        for (i in 0..4)
        {
            reaction_results[i] = 0
        }

        index_of_attempts = 1
        tv_attempts.text = "Attempt $index_of_attempts/5"
        start_attempt()
    }

    fun start_attempt ()
    {
        flag_penalty = false

        tv_to_click.setBackgroundColor(Color.BLACK)
        tv_to_click.isClickable = true

        btn_next_try.isClickable = false
        btn_next_try.visibility = View.INVISIBLE

        btn_reset_attempts.isClickable = false
        btn_reset_attempts.visibility = View.INVISIBLE

        val handler = Handler()

        handler.postDelayed( {

            tv_to_click.text = "3"

        }, 1000)

        handler.postDelayed( {

            tv_to_click.text = "2"

        }, 2000)

        handler.postDelayed( {

            tv_to_click.text = "1"

        }, 3000)

        handler.postDelayed( {

            tv_to_click.text = ""
            flag_game_started = true

        }, 4000)



        handler.postDelayed( {

            tv_to_click.setBackgroundColor(Color.RED)
            start_time = System.nanoTime()
            flag_color_changed = true

        }, Random.nextLong(4500, 10000))
    }

    fun see_result(view: View)
    {
        var avg_value = 0

        for ( i in 0..4)
        {
            avg_value += reaction_results[i].toInt()
            Log.d("Test", "$avg_value ; ${reaction_results[i]}")
        }

        avg_value /= 5

        val intent = Intent(this, game_result::class.java)
        intent.putExtra("avg_reaction", avg_value)
        startActivity(intent)
    }

}