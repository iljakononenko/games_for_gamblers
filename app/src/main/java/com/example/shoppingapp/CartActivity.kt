package com.example.shoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.details.ProductDetailsActivity
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

class CartActivity : AppCompatActivity(), ItemListener, ProductsLoadListener {
    lateinit var productsLoadListener: ProductsLoadListener
    private var adapter: Cart_products_adapter? = null

    private lateinit var accountName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        productsLoadListener = this
        loadProductsFromFirebase()

        accountName = intent.getStringExtra("userName").toString()

        recycler_cart.layoutManager = GridLayoutManager(this, 1)

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
        recycler_cart.adapter = adapter
    }

    override fun onProductsLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun clickedLong(productsModel: Int) {
        val intent = Intent(this, ProductDetailsActivity::class.java).apply {
            putExtra("itemToShow", productsModel.toString())
            putExtra("user", accountName)
        }
        startActivityForResult(intent, 2)
    }
}