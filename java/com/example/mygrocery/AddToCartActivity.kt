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
import java.io.FileOutputStream
import java.io.IOException

class AddToCartActivity : AppCompatActivity() {

    private lateinit var cartProducts: ArrayList<Product>
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private lateinit var totalQuantityText: TextView
    private lateinit var handlingChargeText: TextView
    private lateinit var deliveryChargeText: TextView
    private lateinit var totalPriceBillText: TextView
    private lateinit var paymentButton: Button

    private val handlingCharge = 50 // Example value for handling charge
    private val deliveryCharge = 30 // Example value for delivery charge

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_cart)

        // Get the cart products passed from previous activity or initialize as an empty list
        cartProducts = intent.getParcelableArrayListExtra("cartProducts") ?: arrayListOf()

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceText = findViewById(R.id.totalPrice)
        totalQuantityText = findViewById(R.id.totalQuantity)
        handlingChargeText = findViewById(R.id.handlingCharge)
        deliveryChargeText = findViewById(R.id.deliveryCharge)
        totalPriceBillText = findViewById(R.id.totalPriceBill)
        paymentButton = findViewById(R.id.paymentButton)

        // Set up RecyclerView
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = CartAdapter(cartProducts)

        // Update total price and quantity
        updateTotal()

        // Save cart data
        saveCart()

        // Set payment button action
        paymentButton.setOnClickListener {
            val totalAmount =
                totalPriceBillText.text.toString().replace("Total Price: ₹", "").toInt()
            initiatePhonePePayment(totalAmount)
        }
    }

    // Function to update the total price and quantity
    private fun updateTotal() {
        var totalPrice = 0
        var totalQuantity = 0
        for (product in cartProducts) {
            totalPrice += product.price * product.quantity.toInt()
            totalQuantity += product.quantity.toInt()
        }

        totalPrice += handlingCharge + deliveryCharge

        totalPriceText.text = "Total Price: ₹$totalPrice"
        totalQuantityText.text = "Total Quantity: $totalQuantity"
        handlingChargeText.text = "Handling Charge: ₹$handlingCharge"
        deliveryChargeText.text = "Delivery Charge: ₹$deliveryCharge"
        totalPriceBillText.text = "Total Price: ₹$totalPrice"

        paymentButton.text = "Pay ₹$totalPrice"
    }

    // Function to save the cart to the file in internal storage
    private fun saveCart() {
        val gson = Gson()
        val json = gson.toJson(cartProducts)

        try {
            val file = File(filesDir, "cartData.json")
            FileOutputStream(file).use { fos ->
                fos.write(json.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Function to initiate payment using PhonePe
    private fun initiatePhonePePayment(amount: Int) {
        val phonePePackage = "com.phonepe.app"
        val adminUpiId = "raidivyanka2003@oksbi"
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

    // Handle the result of the payment intent
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
