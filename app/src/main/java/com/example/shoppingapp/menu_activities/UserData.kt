package com.example.shoppingapp.menu_activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.Cart_products_adapter
import com.example.shoppingapp.R
import com.example.shoppingapp.Static_object
import com.example.shoppingapp.details.ProductDetailsActivity
import com.example.shoppingapp.listener.ItemListener
import com.example.shoppingapp.listener.ProductsLoadListener
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.model.User_product_Model
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_data.*

class UserData : AppCompatActivity(), ItemListener, ProductsLoadListener {
    lateinit var productsLoadListener: ProductsLoadListener
    private var adapter: Cart_products_adapter? = null

    private lateinit var accountName : String
    private lateinit var user_id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        accountName = intent.getStringExtra("userName").toString()

        val preferences = getSharedPreferences("USER", Context.MODE_PRIVATE)

        user_id = preferences.getInt("user_id", -1).toString()


        findViewById<TextView>(R.id.tv_name).text = preferences.getString("name", "user name")
        findViewById<TextView>(R.id.tv_email).text = preferences.getString("email", "user e-mail")

        Static_object.ref_current_user
                .child("account_balance").get()
                .addOnSuccessListener {
                    var user_money = it.value as String
                    findViewById<TextView>(R.id.tv_money).text = "$user_money zł"
                }


        //Log.d("Test", user_id)

        productsLoadListener = this
        load_bought_products()
        //loadProductsFromFirebase()

        recycler_user_games.layoutManager = GridLayoutManager(this, 1)
    }

    private fun loadProductsFromFirebase(user_product_models : MutableList<User_product_Model>) {
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

    private fun load_bought_products() {
        val user_product_models : MutableList<User_product_Model> = ArrayList()

        Static_object.ref_products_bought
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

                                user_product_models.add(user_product_model)
                            }
                            loadProductsFromFirebase(user_product_models)
                        }

                    }
                })
    }

    override fun onProductsLoadSuccess(bought_products: List<ProductsModel>?) {

        adapter = Cart_products_adapter(this, bought_products!!,this, false)
        recycler_user_games.adapter = adapter
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

    }
}