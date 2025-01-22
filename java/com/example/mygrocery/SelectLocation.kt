package com.example.mygrocery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.grocery.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import java.util.*

class SelectLocation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var txtAddress: TextView
    private lateinit var edtHouseNo: EditText
    private lateinit var edtFloorNo: EditText
    private lateinit var edtSocietyName: EditText
    private lateinit var edtBlock: EditText
    private lateinit var edtLandmark: EditText
    private lateinit var btnSaveAddress: Button
    private lateinit var btnGetLocation: Button
    private var isAddressSaved = false
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize views
        txtAddress = findViewById(R.id.txtAddress)
        edtHouseNo = findViewById(R.id.edtHouseNo)
        edtFloorNo = findViewById(R.id.edtFloorNo)
        edtSocietyName = findViewById(R.id.edtSocietyName)
        edtBlock = findViewById(R.id.edtBlock)
        edtLandmark = findViewById(R.id.edtLandmark)
        btnSaveAddress = findViewById(R.id.btnSaveAddress)
        btnGetLocation = findViewById(R.id.btnGetLocation)

        // Map setup
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Save address details
        btnSaveAddress.setOnClickListener {
            saveAddress()
        }

        // Get current location on button click
        btnGetLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        mMap.isMyLocationEnabled = true
        mMap.setOnMapClickListener { latLng ->
            updateMarker(latLng)
        }
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                updateMarker(currentLatLng)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
            }
        }
    }

    private fun updateMarker(latLng: LatLng) {
        if (marker == null) {
            marker = mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        } else {
            marker?.position = latLng
        }
        getAddressFromLocation(latLng.latitude, latLng.longitude)
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressText = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    addressText.append(address.getAddressLine(i)).append("\n")
                }
                txtAddress.text = addressText.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveAddress() {
        val consolidatedAddress = buildString {
            append(edtHouseNo.text.toString().trim()).append(", ")
            append(edtFloorNo.text.toString().trim()).append(", ")
            append(edtSocietyName.text.toString().trim()).append(", ")
            append(edtBlock.text.toString().trim()).append(", ")
            append(edtLandmark.text.toString().trim()).append(", ")
            append(txtAddress.text.toString().trim())
        }.trimEnd(',', ' ')

        if (consolidatedAddress.isNotEmpty()) {
            saveToLocalPreferences(consolidatedAddress)
            saveToFirebase(consolidatedAddress)

            val resultIntent = Intent()
            resultIntent.putExtra("selected_location", consolidatedAddress)
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            Toast.makeText(this, "Please complete the address details!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToLocalPreferences(address: String) {
        val sharedPreferences = getSharedPreferences("AddressPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("address", address)
        editor.apply()
        Toast.makeText(this, "Address saved locally!", Toast.LENGTH_SHORT).show()
    }

    private fun saveToFirebase(address: String) {
        val database = FirebaseDatabase.getInstance()
        val addressRef = database.getReference("addresses").push()
        addressRef.setValue(address).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Address saved to Firebase!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save address to Firebase!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }
}
