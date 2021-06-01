package com.example.shoppingapp.menu_activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.Cart_products_adapter
import com.example.shoppingapp.R
import com.example.shoppingapp.listener.ItemListener
import com.example.shoppingapp.listener.ProductsLoadListener
import com.example.shoppingapp.model.ProductsModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_data.*

class UserData : AppCompatActivity(), ItemListener, ProductsLoadListener {
    lateinit var productsLoadListener: ProductsLoadListener
    private var adapter: Cart_products_adapter? = null

    private lateinit var accountName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        productsLoadListener = this
        loadProductsFromFirebase()

        accountName = intent.getStringExtra("userName").toString()

        val preferences = getSharedPreferences("USER", Context.MODE_PRIVATE)

        findViewById<TextView>(R.id.tv_name).text = accountName
        findViewById<TextView>(R.id.tv_email).text = preferences.getString("email", "user e-mail")
        findViewById<TextView>(R.id.tv_money).text = accountName

        recycler_user_games.layoutManager = GridLayoutManager(this, 1)
    }

    private fun loadProductsFromFirebase() {
        val productsModels : MutableList<ProductsModel> = ArrayList()
        FirebaseDatabase.getInstance()
                .getReference("Game")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        productsLoadListener.onProductsLoadFailed(error.message)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            for(productSnapshot in snapshot.children) {
                                val productModel = productSnapshot.getValue(ProductsModel::class.java)
                                productModel!!.key = productSnapshot.key
                                productsModels.add(productModel)
                            }
                            productsLoadListener.onProductsLoadSuccess(productsModels)
                        } else {
                            productsLoadListener.onProductsLoadFailed("Games do not exist")
                        }
                    }
                })
    }

    override fun onProductsLoadSuccess(productsLoadListener: List<ProductsModel>?) {

        adapter = Cart_products_adapter(this, productsLoadListener!!,this)
        recycler_user_games.adapter = adapter
    }

    override fun onProductsLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun clickedLong(productsModel: Int) {

    }
}