package com.example.shoppingapp.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppingapp.MainActivity
import com.example.shoppingapp.R
import com.example.shoppingapp.model.New_User_Model
import com.example.shoppingapp.model.New_User_product_Model
import com.example.shoppingapp.model.User_Model
import com.example.shoppingapp.model.User_product_Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class AccountActivity : AppCompatActivity() {

    private lateinit var activationType : String
    private lateinit var auth: FirebaseAuth

    private var accountName : String = ""
    private var accountEmail : String = ""
    private var accountPassword : String = ""
    private var account_money : Float = 0f
    private var register_flag = false

    private val MY_PREFS_NAME = "USER"
    private val TAG = "DDF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        activationType = intent.getStringExtra("openAccount").toString()
        correctScreen()
        auth = Firebase.auth
    }

    fun logToChat(view: View) {
        accountEmail = findViewById<EditText>(R.id.emailEditText).text.toString()
        accountPassword = findViewById<EditText>(R.id.passwordEditText).text.toString()

        if(activationType == "register")
        {
            accountName =  findViewById<EditText>(R.id.nameEditText).text.toString()
            createAccount(accountEmail, accountPassword,accountName)
        }

        else
        {
            signInAccount(accountEmail, accountPassword)
        }
    }

    private fun createAccount(email: String, password: String, name : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()

                    user!!.updateProfile(profileUpdates).addOnCompleteListener(this)
                    {
                            insertName ->
                        if(insertName.isSuccessful)
                        {
                            Log.d(TAG, "User profile updated.");
                        }
                    }

                    register_flag = true
                    add_user_to_database(user)
                    updateUI(user)
                }
                else
                {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()

                    updateUI(null)
                }
            }
    }

    private fun signInAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                task ->

            if (task.isSuccessful)
                {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    if (user != null)
                    {
                        accountName = if(user.displayName != null)
                        {
                            user?.displayName.toString()
                        }
                        else
                        {
                            user?.email.toString()
                        }
                    }
                    updateUI(user)
                }
                else
                {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()

                    updateUI(null)
                }
            }
    }

    private fun add_user_to_database(account: FirebaseUser?)
    {
        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(
                        object: ValueEventListener
                        {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists())
                                {
                                    var index_of_child = 0

                                    for(productSnapshot in snapshot.children)
                                    {
                                        // liczymy ile jest uzytkownikow zeby dodac kolejnego
                                        index_of_child++
                                    }

                                    val key = FirebaseDatabase.getInstance().getReference("Users").push().key

                                    if (key == null)
                                    {
                                        Log.d("Test", "Nope! You can't just shoot a hole into a Mars!(add a user to a list of users)")
                                        return
                                    }

                                    val new_user = New_User_Model(accountEmail)
                                    val new_user_values = new_user.toMap()

                                    val childUpdates = hashMapOf<String, Any>(
                                            "/$index_of_child" to new_user_values
                                    )

                                    FirebaseDatabase.getInstance().getReference("Users").updateChildren(childUpdates)

                                }

                            }
                        })

    }

    //Change UI according to user data.
    private fun updateUI(account: FirebaseUser?)
    {
        if (account != null)
        {

            //Toast.makeText(this, "U Signed In successfully", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userName", accountName)

            val editor =
                getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()

            editor.putString("name", accountName)
            editor.putString("email", accountEmail)
            editor.putFloat("money", account_money)
            editor.apply()

            write_data_from_account_to_memory()

            startActivity(intent)

        }

        else
        {
            Toast.makeText(this, "U Didn't signed in", Toast.LENGTH_LONG).show()
        }
    }

    private fun write_data_from_account_to_memory()
    {
        var i = 0

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .addListenerForSingleValueEvent(object: ValueEventListener {

                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot)
                    {
                        if(snapshot.exists())
                        {
                            val prefs =  getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)

                            for(user_Snapshot in snapshot.children)
                            {

                                val user_Model = user_Snapshot.getValue(User_Model::class.java)
                                user_Model!!.key = user_Snapshot.key
                                user_Model!!.user_index = i

                                if (accountEmail.equals(user_Model.email))
                                {

                                    var money = user_Model.account_balance
                                    var user_index = user_Model.user_index

                                    if (money != null && user_index != null)
                                    {
                                        prefs.edit().putString("money", money).apply()
                                        prefs.edit().putInt("user_id", user_index).apply()
                                    }

                                    Log.d("Test", "email: ${user_Model.email}, money: $money, user_id: $user_index")

                                }

                                else Log.d("Test", "email: $accountEmail isn't the same as ${user_Model.email}")
                                i++
                            }
                            if (register_flag)
                            {
                                prefs.edit().putFloat("money", 0f).apply()
                                prefs.edit().putInt("user_id", i).apply()
                            }
                        }
                    }
                })
    }

    private fun correctScreen()
    {
        if(activationType == "login")
        {
            findViewById<EditText>(R.id.nameEditText).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.tv_your_name).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.login_title).text = "Login"
        }
        else
        {
            findViewById<TextView>(R.id.login_title).text = "Sign up"
        }
    }
}