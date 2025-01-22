package com.example.mygrocery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R
import com.example.grocery.R.drawable.tooyummmasala

class ChipsActivity : AppCompatActivity() {

    private val cartProducts = mutableListOf<Product>()
    private lateinit var cartDataManager: CartDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chips)

        // Initialize CartDataManager
        cartDataManager = CartDataManager(this)

        // Load previously saved cart products from internal storage
        cartProducts.addAll(cartDataManager.fetchFromInternalStorage())

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val products = listOf(
            Product("Tedhe Medhe Masala Tadka", "500ml", 1, R.drawable.bingotedhe, 1),
            Product("Bingo Spicy Potato Chips", "1L", 1, R.drawable.bingochips, 1),
            Product("Too Yumm Indian Masala Potato Chips", "250ml", 1, tooyummmasala, 1),
            Product("Lay's Potato Chips", "500ml", 1, R.drawable.lays, 1),
            Product("Kurkure, Masala Munch", "500ml", 1, R.drawable.kurkue, 1),
            Product("Peppy Classic Tomato Disc", "500ml", 1, R.drawable.peppy, 1),
            Product("MAGGI 2-Minute Instant Noodles", "Pack Of 12", 153, R.drawable.maggi_front, 1),

        )

        recyclerView.adapter = ProductAdapter(this, products) { product ->
            addToCart(product)
        }

        val cartButton: Button = findViewById(R.id.cartButton)
        cartButton.setOnClickListener {
            cartDataManager.saveToFirebase(cartProducts)
            val intent = Intent(this, AddToCartActivity::class.java)
            intent.putParcelableArrayListExtra("cartProducts", ArrayList(cartProducts))
            startActivity(intent)
        }
    }

    private fun addToCart(product: Product) {
        cartProducts.add(product)
        cartDataManager.saveToInternalStorage(cartProducts)
    }
}

