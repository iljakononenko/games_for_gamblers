package com.example.shoppingapp.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shoppingapp.MainActivity
import com.example.shoppingapp.R
import com.example.shoppingapp.menu_activities.UserData
import com.example.shoppingapp.model.CartModel
import com.example.shoppingapp.model.New_User_product_Model
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.model.User_product_Model
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*


class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var userName: String
    private lateinit var message: String
    private var item: ProductsModel? = null
    private var cartModel: CartModel? = null
    private var product_id: Int? = null
    private lateinit var user_id : String

    override fun onCreate(savedInstanceState: Bundle?) {

        val preferences = getSharedPreferences("USER", Context.MODE_PRIVATE)
        user_id = preferences.getInt("user_id", -1).toString()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        message = intent.getStringExtra("itemToShow").toString()
        userName = intent.getStringExtra("user").toString()
        currentFireBase()
    }

    private fun currentFireBase() {
        FirebaseDatabase.getInstance().getReference("Game").child(message).addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled", " cancelled")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val itemData = snapshot.getValue<ProductsModel>(ProductsModel::class.java)
                    if (itemData != null) {
                        try {
                            item = itemData
                            prepareActivityWithProductDetails(itemData)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("TAG", " it's null.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        FirebaseDatabase.getInstance().getReference("Cart").child(userName).addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                Log.e("onCancelled", " cancelled")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    cartModel = snapshot.getValue<CartModel>(CartModel::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun prepareActivityWithProductDetails(itemData: ProductsModel) {
        product_id = itemData.product_id
        val itemImageView = findViewById<ImageView>(R.id.itemImageView)
        Picasso.get().load(itemData.image).into(itemImageView)
        findViewById<TextView>(R.id.itemNameTextView).text = itemData.name
        findViewById<TextView>(R.id.itemPriceTextView).text = itemData.price + " zł"
        findViewById<TextView>(R.id.itemDetailsTextView).text = itemData.description
    }

    fun addToCart(view: View) {
        databaseAdd()
    }

    private fun databaseAdd() {

        cartModel!!.quantity++
        cartModel!!.totalPrice += item!!.price!!.toFloat()

        FirebaseDatabase.getInstance().getReference("Cart").child(userName).setValue(cartModel)

        // elijah added to user child (cart)

        var flag_product_amount_incremented = false
        var index_of_child = 0

        FirebaseDatabase.getInstance().getReference("Users")
                .child(user_id)
                .child("Products_in_cart")
                .addListenerForSingleValueEvent(
                        object: ValueEventListener
                        {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists())
                                {
                                    for(productSnapshot in snapshot.children)
                                    {
                                        val user_product_model = productSnapshot.getValue(User_product_Model::class.java)
                                        user_product_model!!.key = productSnapshot.key

                                        if (user_product_model.product_id == product_id)
                                        {

                                            // jak znajdziemy odpowiedni produkt w koszyku, inkrementujemy liczbe w tym koszyku

                                            flag_product_amount_incremented = true
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(user_id)
                                                    .child("Products_in_cart")
                                                    .child(user_product_model.key.toString())
                                                    //.child(index_of_child.toString()) - dodaje dziecko
                                                    .child("product_amount")
                                                    .setValue(user_product_model.product_amount?.plus(1))
                                        }
                                        index_of_child++
                                    }

                                    if (flag_product_amount_incremented == false)
                                    {
                                        // jak sie nie uda znalezc w koszyku odpowiedni produkt, wtedy musimy dodac go do koszyka

                                        val key = FirebaseDatabase.getInstance().getReference("Users")
                                                .child(user_id)
                                                .child("Products_in_cart")
                                                .push().key
                                        if (key == null)
                                        {
                                            Log.d("Test", "Nope! You can't just shoot a hole into a Mars!(add a product to a cart)")
                                            return
                                        }
                                        val new_product_in_cart = New_User_product_Model(product_id)
                                        val new_product_in_cart_values = new_product_in_cart.toMap()

                                        val childUpdates = hashMapOf<String, Any>(
                                                "/$user_id/Products_in_cart/$key" to new_product_in_cart_values
                                        )

                                        FirebaseDatabase.getInstance().getReference("Users").updateChildren(childUpdates)
                                    }
                                }

                            }
                        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("userName", userName)
        }
        startActivity(intent)
        finish()
    }

}