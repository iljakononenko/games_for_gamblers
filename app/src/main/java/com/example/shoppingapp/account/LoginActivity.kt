package com.example.shoppingapp.account

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.shoppingapp.MainActivity
import com.example.shoppingapp.R
import com.example.shoppingapp.model.CartModel
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.model.User_Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    private val MY_PREFS_NAME = "USER"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prefs =  getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE) //check if user logged before

        val name =
            prefs.getString("name", "No name defined") //"No name defined" is the default value.

        val email = prefs.getString("email", "No email defined")

        //case if user logged before

        var i = 0

        if(name != null && name != "No name defined")
        {
            val new_intent = Intent(this, MainActivity::class.java)
            new_intent.putExtra("userName", name)

            FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .addListenerForSingleValueEvent(object: ValueEventListener {

                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot)
                        {
                            if(snapshot.exists())
                            {

                                for(user_Snapshot in snapshot.children)
                                {

                                    val user_Model = user_Snapshot.getValue(User_Model::class.java)
                                    user_Model!!.key = user_Snapshot.key
                                    user_Model!!.user_index = i

                                    if (email.equals(user_Model.email))
                                    {
                                        write_data_from_account_to_memory(user_Model, new_intent)
                                    }

                                    else Log.d("Test", "email: $email isn't the same as ${user_Model.email}")
                                    i++
                                }
                            }
                        }
                    })


        }
    }

    fun write_data_from_account_to_memory (user_Model: User_Model, new_intent : Intent)
    {
        val prefs =  getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        var money = user_Model.account_balance
        var user_index = user_Model.user_index

        if (money != null && user_index != null)
        {
            prefs.edit().putFloat("money", money).apply()
            prefs.edit().putInt("user_id", user_index).apply()
        }

        Log.d("Test", "email: ${user_Model.email}, money: $money, user_id: $user_index")

        startActivity(new_intent)
    }

    /*
     * Log to the account
     */
    fun loginToAccount(view: View) {
        val intent = Intent(this, AccountActivity::class.java).apply {
            putExtra("openAccount","login")
        }
        startActivity(intent)
    }

    /*
     * Register new account
     */
    fun registerAccount(view: View) {
        val intent = Intent(this, AccountActivity::class.java).apply {
            putExtra("openAccount","register")
        }
        startActivity(intent)
    }
}