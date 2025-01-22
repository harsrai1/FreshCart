package com.example.mygrocery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R

class MilkCategoryActivity : AppCompatActivity() {

    private lateinit var cartDataManager: CartDataManager
    private val cartProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_milk_category)

        // Initialize CartDataManager
        cartDataManager = CartDataManager(this)

        // Load previously saved cart products from internal storage
        cartProducts.addAll(cartDataManager.fetchFromInternalStorage())

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView2)

        // Set GridLayoutManager to create 2 columns
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Dummy data for products
        val products = listOf(
            Product("Amul Taaza Toned Milk", "500ml", 1, R.drawable.amul_taaza_toned_milk, 1),
            Product("Amul Taaza Homogenised Milk", "1L", 1, R.drawable.amul_taaza_homogenised, 1),
            Product("Amul Lactose Free Milk", "250ml", 1, R.drawable.amullactose, 1),
            Product("Humpy Farms A2 Cow Milk", "500ml", 1, R.drawable.humpy_farms_cow_milk, 1)
        )

        // Set up RecyclerView with adapter
        recyclerView.adapter = ProductAdapter(this, products) { product ->
            addToCart(product)
        }

        // Initialize cart button and set click listener
        val cartButton: Button = findViewById(R.id.cartButton)
        cartButton.setOnClickListener {
            cartDataManager.saveToFirebase(cartProducts)
            val intent = Intent(this, AddToCartActivity::class.java)
            intent.putParcelableArrayListExtra("cartProducts", ArrayList(cartProducts))
            startActivity(intent)
        }
    }

    private fun addToCart(product: Product) {
        val existingProduct = cartProducts.find { it.name == product.name }
        if (existingProduct != null) {
            existingProduct.quantity += 1 // Increase quantity if already in cart
        } else {
            product.quantity = 1 // Set quantity to 1 if it's the first time adding the product
            cartProducts.add(product)
        }
        cartDataManager.saveToInternalStorage(cartProducts)
    }
}
