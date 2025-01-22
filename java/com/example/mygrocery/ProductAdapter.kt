package com.example.mygrocery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R

class ProductAdapter(
    private val context: Context,
    private val products: List<Product>,
    private val onAddToCartClick: (Product) -> Unit // Callback for adding products to the cart
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productSize: TextView = itemView.findViewById(R.id.productSize)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val addButton: Button = itemView.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productImage.setImageResource(product.imageRes)
        holder.productName.text = product.name
        holder.productSize.text = product.size
        holder.productPrice.text = "₹${product.price}"

        holder.addButton.setOnClickListener {
            // Call the function passed in the constructor to handle adding to the cart
            onAddToCartClick(product)
        }
    }

    override fun getItemCount(): Int = products.size
}
