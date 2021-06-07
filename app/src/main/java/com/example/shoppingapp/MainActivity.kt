package com.example.shoppingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingapp.account.LoginActivity
import com.example.shoppingapp.details.ProductDetailsActivity
import com.example.shoppingapp.listener.ItemListener
import com.example.shoppingapp.listener.ProductsLoadListener
import com.example.shoppingapp.menu_activities.MapsActivity
import com.example.shoppingapp.menu_activities.UserData
import com.example.shoppingapp.model.CartModel
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.model.User_product_Model
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ProductsLoadListener,
    ItemListener {

    lateinit var productsLoadListener: ProductsLoadListener
    private var adapter: ProductsAdapter? = null
    private lateinit var accountName : String
    private var MY_PREFS_NAME = "USER"
    private lateinit var user_id : String

    override fun onStart() {
        super.onStart()
        countCartFRomFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Static_object.preferences = getSharedPreferences("USER", Context.MODE_PRIVATE)
        Static_object.update_data()

        val preferences = getSharedPreferences("USER", Context.MODE_PRIVATE)
        user_id = preferences.getInt("user_id", -1).toString()

        accountName = intent.getStringExtra("userName").toString()
        init()
        loadProductsFromFirebase()
        countCartFRomFirebase()
    }

    private fun init() {
        productsLoadListener = this
        val gridLayoutManager = GridLayoutManager(this, 2)
        recycler_drink.layoutManager = gridLayoutManager
        recycler_drink.addItemDecoration(SpaceItemDecoration())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() //polega na tym ze czysci back stack
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        Log.i("UI_INFO", "Selected Item: " + item.title)
        return when (item.itemId) {
            R.id.maps_item ->
            {
                mapClicked()
                true
            }
            R.id.logout_item ->
            {
                val preferences = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
                preferences.edit().remove("name").apply()
                preferences.edit().remove("pass").apply()
                preferences.edit().remove("email").apply()
                preferences.edit().remove("money").apply()
                preferences.edit().remove("user_id").apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.account_item ->
            {
                val intent = Intent(this, UserData::class.java).apply {
                    putExtra("userName", accountName)
                }
                startActivity(intent)
                true
            }
            R.id.game_item ->
            {
                val intent = Intent(this, Entertainment_decide::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mapClicked() {
        val intent = Intent(this, MapsActivity::class.java).apply {
            putExtra("showMap", "" )
        }
        startActivityForResult(intent, 1)
    }

    private fun countCartFRomFirebase() {

        Static_object.ref_products_in_cart
                .addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                var number_of_items_in_cart = 0

                for (product_in_bought_list in snapshot.children)
                {
                    number_of_items_in_cart += product_in_bought_list.getValue(User_product_Model::class.java)?.product_amount!!.toInt()
                }

                badge!!.setNumber(number_of_items_in_cart)
            }
        })
    }

    private fun loadProductsFromFirebase() {
        val productsModels : MutableList<ProductsModel> = ArrayList()

        FirebaseDatabase.getInstance().getReference("Game")
                .addListenerForSingleValueEvent(
                object: ValueEventListener
                {
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
        adapter = ProductsAdapter(this,productsLoadListener!!,this)
        recycler_drink.adapter = adapter
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

    fun cartShow(view: View) {
        val intent = Intent(this, CartActivity::class.java).apply {
            putExtra("userName", accountName)
        }
        startActivityForResult(intent, 3)
    }
}