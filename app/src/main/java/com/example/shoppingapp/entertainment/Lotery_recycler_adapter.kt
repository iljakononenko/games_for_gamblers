package com.example.shoppingapp.entertainment

import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import kotlinx.android.synthetic.main.btn_square.view.*

class Lotery_recycler_adapter (private val dataSet: List<Int>, private val listener: my_OnItemClickListener) : RecyclerView.Adapter<Lotery_recycler_adapter.ViewPagerViewHolder>(){

    var button_pressed_counter = 0

    inner class ViewPagerViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val btn_square: Button = itemView.findViewById(R.id.btn_square)
    }

    interface my_OnItemClickListener
    {
        fun check_lotery_result (position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.btn_square, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {

        holder.btn_square.setTextColor(Color.WHITE)
        holder.btn_square.setBackgroundColor(Color.YELLOW)
        holder.btn_square.text = dataSet[position].toString()
        holder.btn_square.textSize = 0F
        holder.btn_square.setOnClickListener(View.OnClickListener {
            if (button_pressed_counter < 3)
            {
                holder.btn_square.textSize = 24f
                holder.btn_square.isClickable = false
                holder.btn_square.setBackgroundColor(Color.BLACK)
                button_pressed_counter++
                listener.check_lotery_result(position)
            }
        })
    }

}