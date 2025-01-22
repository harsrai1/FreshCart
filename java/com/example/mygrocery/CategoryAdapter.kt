package com.example.mygrocery

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R

class CategoryAdapter(
    private val categoryList: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.grid_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name
        holder.categoryImage.setImageResource(category.imageRes)

        // Set click listener to navigate to the corresponding category activity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            when (category.name) {
                "Munchies" -> {
                    // Start ChipsActivity when "Munchies" is clicked
                    val intent = Intent(context, ChipsActivity::class.java)
                    context.startActivity(intent)
                }
                "Dairy & Breakfast" -> {
                    // Navigate to MilkCategoryActivity when "Milk" is clicked
                    val intent = Intent(context, MilkCategoryActivity::class.java)
                    context.startActivity(intent)
                }

                // You can add more categories and their respective activities here
            }
        }
    }

    override fun getItemCount(): Int = categoryList.size
}
