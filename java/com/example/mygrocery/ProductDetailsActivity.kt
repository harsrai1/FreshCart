package com.example.mygrocery

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.grocery.R

class ProductDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val imageContainer = findViewById<LinearLayout>(R.id.imageContainer)

        // List of image resources to be displayed in the scrollable container
        val imageResources = listOf(
            R.drawable.maggi1,
            R.drawable.maggi2,
            R.drawable.maggi3
        )

        // Dynamically add ImageViews to the container
        for (res in imageResources) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                    marginEnd = 8 // Add spacing between images
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(res)
            }
            imageContainer.addView(imageView)
        }
    }
}
