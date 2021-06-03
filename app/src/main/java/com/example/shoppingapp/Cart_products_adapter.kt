package com.example.shoppingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.model.ProductsModel
import com.example.shoppingapp.listener.ItemListener
import java.lang.StringBuilder

class Cart_products_adapter(private val context: Context, private val list: List<ProductsModel>, private val itemListener: ItemListener)
    : RecyclerView.Adapter<Cart_products_adapter.ProductsViewHolder>() {

    inner class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var txtName: TextView? = null
        var txtPrice: TextView? = null
        var btn_delete: ImageButton = itemView.findViewById(R.id.btn_remove_from_cart)

        init {
            imageView = itemView.findViewById(R.id.iv_cart_icon) as ImageView
            txtName = itemView.findViewById(R.id.tv_game_name) as TextView
            txtPrice = itemView.findViewById(R.id.tv_game_price) as TextView

            itemView.setOnClickListener {
                itemListener.see_product_details( list.get(adapterPosition).product_id )
            }

            btn_delete.setOnClickListener {
                itemListener.delete_product_from_cart( list.get(adapterPosition).product_id )
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        Glide.with(context)
                .load(list[position].image)
                .into(holder.imageView!!)
        holder.txtName!!.text  = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text  = StringBuilder().append(list[position].price+" zł")
    }
}
