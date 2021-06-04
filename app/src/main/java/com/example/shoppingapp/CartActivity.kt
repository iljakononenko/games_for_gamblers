package com.example.shoppingapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.details.ProductDetailsActivity
import com.example.shoppingapp.listener.ItemListener
import com.example.shoppingapp.listener.ProductsLoadListener
import com.example.shoppingapp.model.New_User_product_Model
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.model.User_product_Model
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class CartActivity : AppCompatActivity(), ItemListener, ProductsLoadListener {
    lateinit var productsLoadListener: ProductsLoadListener
    private var adapter: Cart_products_adapter? = null

    private lateinit var accountName : String
    private lateinit var user_id : String
    private var money = "-1f"
    private var sum_of_products = 0f
    private var cart_is_empty = true
    private var buy_flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val preferences = getSharedPreferences("USER", Context.MODE_PRIVATE)
        user_id = preferences.getInt("user_id", -1).toString()

        accountName = intent.getStringExtra("userName").toString()
        money = preferences.getString("money", "-1f")!!

        productsLoadListener = this
        load_products_in_cart()

        recycler_cart.layoutManager = GridLayoutManager(this, 1)

    }

    private fun displayProductsFromFirebase(user_product_models : MutableList<User_product_Model>) {
        val productsModels : MutableList<ProductsModel> = ArrayList()

        FirebaseDatabase.getInstance().getReference("Game")
                .addListenerForSingleValueEvent(
                        object: ValueEventListener
                        {
                            override fun onCancelled(error: DatabaseError) {
                                productsLoadListener.onProductsLoadFailed(error.message)
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists())
                                {
                                    for (user_product_Model in user_product_models)
                                    {
                                        for(productSnapshot in snapshot.children)
                                        {
                                            val productModel = productSnapshot.getValue(ProductsModel::class.java)
                                            productModel!!.key = productSnapshot.key

                                            if (user_product_Model.product_id == productModel.product_id )
                                            {
                                                Log.d("Test", "Still calls!")
                                                productModel.quantity = user_product_Model.product_amount
                                                productsModels.add(productModel)
                                            }
                                        }
                                    }
                                    productsLoadListener.onProductsLoadSuccess(productsModels)
                                } else {
                                    productsLoadListener.onProductsLoadFailed("Games do not exist")
                                }
                            }
                        })
    }

    private fun load_products_in_cart() {
        val user_product_models : MutableList<User_product_Model> = ArrayList()

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
                                        cart_is_empty = false

                                        val user_product_model = productSnapshot.getValue(User_product_Model::class.java)
                                        user_product_model!!.key = productSnapshot.key

                                        user_product_models.add(user_product_model)
                                    }

                                    if (buy_flag)
                                    {
                                        move_from_cart_to_bought(user_product_models)
                                        buy_flag = false
                                    }
                                    else
                                    {
                                        displayProductsFromFirebase(user_product_models)
                                    }

                                }

                            }
                        })
    }

    override fun onProductsLoadSuccess(user_product_models: List<ProductsModel>?) {

        sum_of_products = 0f

        if (user_product_models != null)
        {

            for (product: ProductsModel in user_product_models)
            {
                sum_of_products += (product.price!!.toFloat() * product.quantity!!.toFloat())
            }

            adapter = Cart_products_adapter(this, user_product_models,this, true)
            recycler_cart.adapter = adapter

        }

        findViewById<TextView>(R.id.tv_summary_price).text = "$sum_of_products zł"

    }

    override fun onProductsLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun see_product_details(productsModel: Int?) {
        val intent = Intent(this, ProductDetailsActivity::class.java).apply {
            putExtra("itemToShow", productsModel.toString())
            putExtra("user", accountName)
        }
        startActivityForResult(intent, 2)
    }

    override fun delete_product_from_cart(product_id_to_remove: Int?) {

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
                                    var number_of_items_in_cart = 0
                                    var key_of_product_to_remove: String? = "null"

                                    for(productSnapshot in snapshot.children)
                                    {
                                        val user_product_model = productSnapshot.getValue(User_product_Model::class.java)
                                        user_product_model!!.key = productSnapshot.key

                                        if (user_product_model.product_id == product_id_to_remove)
                                        {
                                            key_of_product_to_remove = productSnapshot.key
                                        }
                                        number_of_items_in_cart += user_product_model.product_amount!!
                                    }

                                    if (key_of_product_to_remove != null && key_of_product_to_remove != "null" && number_of_items_in_cart > 1 )
                                    {
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(user_id)
                                                .child("Products_in_cart")
                                                .child("$key_of_product_to_remove")
                                                .child("product_amount").get().addOnSuccessListener {

                                                    var number_of_selected_items = it.value as Long

                                                    if (number_of_selected_items.toInt() == 1)
                                                    {
                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(user_id)
                                                                .child("Products_in_cart")
                                                                .child("$key_of_product_to_remove").removeValue()
                                                    }
                                                    else
                                                    {
                                                        FirebaseDatabase.getInstance().getReference("Users")
                                                                .child(user_id)
                                                                .child("Products_in_cart")
                                                                .child("$key_of_product_to_remove")
                                                                .child("product_amount").setValue( (number_of_selected_items.toInt() - 1) )
                                                    }

                                                }
                                    }

                                    else if (key_of_product_to_remove != null && key_of_product_to_remove != "null")
                                    {
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(user_id)
                                                .child("Products_in_cart").setValue(0)
                                        cart_is_empty = true
                                    }

                                    load_products_in_cart()
                                }

                            }
                        })
    }

    fun buy_all(view: View)
    {

        if ( cart_is_empty )
        {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
        }
        else
        {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(user_id)
                    .child("account_balance").get()
                    .addOnSuccessListener {

                        var user_money = it.value as String

                        if (sum_of_products < user_money.toFloat())
                        {

                            var new_balance = user_money.toFloat() - sum_of_products

                            val df = DecimalFormat("#.##")
                            df.roundingMode = RoundingMode.CEILING
                            df.format(new_balance).toFloat()

                            user_money = new_balance.toString()

                            Log.d("Test", "user money are: $user_money")

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(user_id)
                                    .child("account_balance")
                                    .setValue(user_money)

                            buy_flag = true

                            load_products_in_cart()

                        }
                        else
                        {
                            Toast.makeText(this, "Sorry, you don't have enough money!", Toast.LENGTH_SHORT).show()
                        }
                    }

        }

    }

    fun move_from_cart_to_bought(products_in_cart : MutableList<User_product_Model>)
    {
        val products_models : MutableList<ProductsModel> = ArrayList()

        FirebaseDatabase.getInstance().getReference("Users")
                .child(user_id)
                .child("Products_bought")
                .addListenerForSingleValueEvent(
                        object: ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                productsLoadListener.onProductsLoadFailed(error.message)
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    Log.d("Test", "Buying games!")

                                    for (product_in_cart in products_in_cart) {

                                        Log.d("Test", "Buying a game!")

                                        var flag_product_amount_incremented = false

                                        for (product_in_bought_list in snapshot.children) {
                                            val user_product_model = product_in_bought_list.getValue(User_product_Model::class.java)
                                            user_product_model!!.key = product_in_bought_list.key

                                            if (user_product_model.product_id == product_in_cart.product_id) {

                                                Log.d("Test", "A game is already in bought list!")

                                                // jak znajdziemy odpowiedni produkt w koszyku, inkrementujemy liczbe w tym koszyku

                                                FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(user_id)
                                                        .child("Products_bought")
                                                        .child(user_product_model.key.toString())
                                                        //.child(index_of_child.toString()) - dodaje dziecko
                                                        .child("product_amount")
                                                        .setValue(user_product_model.product_amount?.plus(product_in_cart.product_amount!!))

                                                flag_product_amount_incremented = true
                                            }

                                        }

                                        if (flag_product_amount_incremented == false) {
                                            // jak sie nie uda znalezc w koszyku odpowiedni produkt, wtedy musimy dodac go do koszyka

                                            Log.d("Test", "Added new game to a bought list!")

                                            val key = FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(user_id)
                                                    .child("Products_bought")
                                                    .push().key
                                            if (key == null) {
                                                Log.d("Test", "Nope! You can't just shoot a hole into a Mars!(add a product to a bought list)")
                                                return
                                            }

                                            val new_product_to_bought_list = New_User_product_Model(product_in_cart.product_id, product_in_cart.product_amount)
                                            val new_product_to_bought_list_values = new_product_to_bought_list.toMap()

                                            val childUpdates = hashMapOf<String, Any>(
                                                    "/$user_id/Products_bought/$key" to new_product_to_bought_list_values
                                            )

                                            FirebaseDatabase.getInstance().getReference("Users").updateChildren(childUpdates)
                                        }

                                    }

                                }
                            }
                        })
        FirebaseDatabase.getInstance().getReference("Users")
                .child(user_id)
                .child("Products_in_cart")
                .setValue(0)

        load_products_in_cart()
    }
}