package com.example.mygrocery

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val name: String,
    val size: String,
    val price: Int,
    val imageRes: Int, // Resource ID for the product image
    var quantity: Int = 1 // Quantity of the product in the cart
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt() // Read quantity as an integer
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(size)
        parcel.writeInt(price)
        parcel.writeInt(imageRes)
        parcel.writeInt(quantity) // Write quantity as an integer
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
