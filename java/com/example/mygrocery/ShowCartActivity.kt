package com.example.mygrocery

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R
import com.google.gson.Gson
import java.io.File
import java.io.IOException

class ShowCartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private lateinit var totalQuantityText: TextView
    private lateinit var paymentButton: Button
    private lateinit var handlingChargeText: TextView
    private lateinit var deliveryChargeText: TextView
    private lateinit var totalPriceBillText: TextView

    private lateinit var cartAdapter: CartAdapter
    private val cartProducts = ArrayList<Product>()

    private val handlingCharge = 20
    private val deliveryCharge = 30

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_cart)

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceText = findViewById(R.id.totalPrice)
        totalQuantityText = findViewById(R.id.totalQuantity)
        paymentButton = findViewById(R.id.paymentButton)
        handlingChargeText = findViewById(R.id.handlingCharge)
        deliveryChargeText = findViewById(R.id.deliveryCharge)
        totalPriceBillText = findViewById(R.id.totalPriceBill)

        // Set up RecyclerView
        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the cart adapter
        cartAdapter = CartAdapter(cartProducts)
        cartRecyclerView.adapter = cartAdapter

        // Load cart data from JSON file
        loadCart()

        // Update total price and quantity
        updateTotal()

        // Set payment button action
        paymentButton.setOnClickListener {
            val totalAmount =
                totalPriceBillText.text.toString().replace("Grand total: ₹", "").toInt()
            initiatePhonePePayment(totalAmount)
        }
    }

    // Function to load the cart from the JSON file
    private fun loadCart() {
        val gson = Gson()
        val file = File(filesDir, "cartData.json")

        if (file.exists()) {
            try {
                val json = file.readText()
                val products = gson.fromJson(json, Array<Product>::class.java).toList()
                cartProducts.clear()
                cartProducts.addAll(products)
                cartAdapter.notifyDataSetChanged()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error reading cart data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to update the total price and quantity
    private fun updateTotal() {
        var totalPrice = 0
        var totalQuantity = 0
        for (product in cartProducts) {
            totalPrice += product.price * product.quantity
            totalQuantity += product.quantity
        }

        totalPrice += handlingCharge + deliveryCharge

        totalPriceText.text = "Total Price: ₹$totalPrice"
        totalQuantityText.text = "Total Quantity: $totalQuantity"
        handlingChargeText.text = "Handling charge: ₹$handlingCharge"
        deliveryChargeText.text = "Delivery charge: ₹$deliveryCharge"
        totalPriceBillText.text = "Grand total: ₹$totalPrice"

        paymentButton.text = "Pay ₹$totalPrice"
    }

    // Initiate PhonePe payment
    private fun initiatePhonePePayment(amount: Int) {
        val phonePePackage = "com.phonepe.app"
        val adminUpiId = "7415758987@ybl"
        val companyName = "FatakBazaar"

        val upiUri = Uri.parse("upi://pay?pa=$adminUpiId&pn=$companyName&am=$amount&cu=INR")
        val intent = Intent(Intent.ACTION_VIEW, upiUri)
        intent.setPackage(phonePePackage)

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1)
        } else {
            Toast.makeText(this, "PhonePe is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
