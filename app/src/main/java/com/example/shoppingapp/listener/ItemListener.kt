package com.example.shoppingapp.listener

interface ItemListener {
    fun see_product_details(productsModel: Int?)
    fun delete_product_from_cart(product_id_to_remove: Int?)
}
