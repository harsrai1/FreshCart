package com.example.mygrocery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R

class CartAdapter(private val products: List<Product>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productSize: TextView = itemView.findViewById(R.id.productSize)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = products[position]

        // Set the product image, name, size, and quantity
        holder.productImage.setImageResource(product.imageRes)
        holder.productName.text = product.name
        holder.productSize.text = product.size
        holder.productQuantity.text = "Quantity: ${product.quantity}"

        // Calculate the total price for each product (price * quantity)
        val totalPrice = product.price * product.quantity
        holder.productPrice.text = "₹$totalPrice"
    }

    override fun getItemCount(): Int = products.size
}
