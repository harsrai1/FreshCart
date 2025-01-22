package com.example.mygrocery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.grocery.R

class PaymentOptionsActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_options)

        radioGroup = findViewById(R.id.radioGroup)
        confirmButton = findViewById(R.id.confirmButton)

        confirmButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedPaymentMethod = findViewById<RadioButton>(selectedId).text.toString()
                val intent = Intent()
                intent.putExtra("selectedPaymentMethod", selectedPaymentMethod)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}
