package com.example.shoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.shoppingapp.entertainment.Game
import com.example.shoppingapp.entertainment.Lotery

class Entertainment_decide : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entertainment_decide)
    }

    fun decide_entertainment(view: View) {
        if (findViewById<Button>(R.id.btn_play_game) == view)
        {
            val intent = Intent(this, Game::class.java)
            startActivity(intent)
        }
        else if (findViewById<Button>(R.id.btn_play_lotery) == view)
        {
            val intent = Intent(this, Lotery::class.java)
            startActivity(intent)
        }
        else
        {
            Toast.makeText(this, "Sorry, something went wrong!", Toast.LENGTH_SHORT)
        }

    }
}