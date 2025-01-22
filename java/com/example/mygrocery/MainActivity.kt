package com.example.mygrocery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R

class MainActivity : AppCompatActivity() {

    private lateinit var hintSwitcher: TextSwitcher
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryList = ArrayList<Category>()

    private lateinit var locationSpinner: Spinner
    private lateinit var locationAdapter: ArrayAdapter<String>
    private val savedLocations = mutableListOf<String>()
    private val defaultLocations = listOf("Select Location", "Use current location")
    private val preferencesName = "MyGroceryPrefs"

    private val hints = listOf("Search egg", "Search chips", "Search milk", "Search bread", "Search face wash")
    private var hintIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TextSwitcher for search hints
        hintSwitcher = findViewById(R.id.hint_switcher)
        hintSwitcher.setFactory {
            TextView(this).apply {
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.darker_gray, theme))
            }
        }
        hintSwitcher.inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        hintSwitcher.outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        startHintAnimation()

        // Account and cart buttons
        val accountIcon: ImageView = findViewById(R.id.account_icon)
        accountIcon.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        val cartButton: LinearLayout = findViewById(R.id.cart_button)
        cartButton.setOnClickListener {
            val intent = Intent(this, ShowCartActivity::class.java)
            startActivity(intent)
        }

        // Spinner for location selection
        locationSpinner = findViewById(R.id.location_spinner)
        loadSavedLocations()

        locationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, savedLocations)
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = locationAdapter

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLocation = parent.getItemAtPosition(position).toString()
                if (selectedLocation == "Use current location") {
                    val intent = Intent(this@MainActivity, SelectLocation::class.java)
                    startActivityForResult(intent, REQUEST_LOCATION)
                } else if (selectedLocation != "Select Location") {
                    saveSelectedLocation(selectedLocation)
                    Toast.makeText(this@MainActivity, "Location Selected: $selectedLocation", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // RecyclerView for categories
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        loadCategories()
        categoryAdapter = CategoryAdapter(categoryList)
        recyclerView.adapter = categoryAdapter
    }

    private fun startHintAnimation() {
        handler.post(object : Runnable {
            override fun run() {
                hintSwitcher.setText(hints[hintIndex])
                hintIndex = (hintIndex + 1) % hints.size
                handler.postDelayed(this, 3000)
            }
        })
    }

    private fun saveSelectedLocation(location: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("current_location", location)
        editor.apply()
    }

    private fun getSavedLocation(): String {
        val sharedPreferences: SharedPreferences = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        return sharedPreferences.getString("current_location", "") ?: ""
    }

    private fun loadSavedLocations() {
        val sharedPreferences = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        savedLocations.clear()
        savedLocations.addAll(defaultLocations)
        savedLocations.addAll(sharedPreferences.getStringSet("saved_locations", emptySet()) ?: emptySet())
    }

    private fun saveLocations() {
        val sharedPreferences = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("saved_locations", savedLocations.toSet())
        editor.apply()
    }

    private fun loadCategories() {
        categoryList.add(Category("Vegetables & Fruits", R.drawable.veg_fruits))
        categoryList.add(Category("Dairy & Breakfast", R.drawable.dairy))
        categoryList.add(Category("Munchies", R.drawable.munchies))
        categoryList.add(Category("Cold Drinks & Juices", R.drawable.drinks))
        categoryList.add(Category("Instant Food", R.drawable.instant_food))
        categoryList.add(Category("Tea, Coffee & Drinks", R.drawable.tea_coffee))
        categoryList.add(Category("Bakery & Biscuits", R.drawable.bakery))
        categoryList.add(Category("Sweet Tooth", R.drawable.sweet_tooth))
        categoryList.add(Category("Atta, Rice & Dal", R.drawable.atta_rice_dal))
        categoryList.add(Category("Dry Fruits & Oil", R.drawable.dry_fruits))
        categoryList.add(Category("Sauces & Spreads", R.drawable.sauces))
        categoryList.add(Category("Cleaning Essentials", R.drawable.cleaning))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK) {
            val selectedLocation = data?.getStringExtra("selected_location") ?: ""
            if (selectedLocation.isNotEmpty() && !savedLocations.contains(selectedLocation)) {
                savedLocations.add(selectedLocation)
                saveLocations()
                locationAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Location updated: $selectedLocation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION = 1
    }
}
