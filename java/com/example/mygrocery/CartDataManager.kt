package com.example.mygrocery

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartDataManager(private val context: Context) {

    // Firebase Database reference
    private val firebaseDatabase = FirebaseDatabase.getInstance().getReference("cartProducts")

    // SharedPreferences for internal storage
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)

    private val gson = Gson()

    // Save products to Firebase
    fun saveToFirebase(cartProducts: List<Product>) {
        firebaseDatabase.setValue(cartProducts)
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    // Save products to internal storage
    fun saveToInternalStorage(cartProducts: List<Product>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(cartProducts)
        editor.putString("cartProducts", json)
        editor.apply()
    }

    // Fetch products from internal storage
    fun fetchFromInternalStorage(): List<Product> {
        val json = sharedPreferences.getString("cartProducts", null) ?: return emptyList()
        val type = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(json, type)
    }
}
