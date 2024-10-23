package com.example.unitconverter

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unitconverter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //instead of var conversionSpinner: Spinner
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Inflating layout and assigning the binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Connecting array with spinner
        val conversionModes = resources.getStringArray(R.array.conversion_modes)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, conversionModes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.conversionSpinner.adapter = adapter

        // binding + OnClickListener for convert-button, calls convert method
        binding.convertButton.setOnClickListener {
            val inputValue = binding.inputValue.text.toString().toDoubleOrNull()

            if (inputValue != null) {
                val selectedMode = binding.conversionSpinner.selectedItem.toString()
                val result = convert(selectedMode, inputValue)
                binding.outputValue.text = result.toString()
            } else {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convert(mode: String, value: Double): Double {
        return when (mode) {
            "Meter to Inch" -> value * 39.3701
            "Inch to Meter" -> value / 39.3701
            "Celsius to Fahrenheit" -> value * 9 / 5 + 32
            "Fahrenheit to Celsius" -> (value - 32) * 5 / 9
            "Centimeter to Inch" -> value * 0.393701
            "Inch to Centimeter" -> value / 0.393701
            else -> value // Fallback: No conversion
        }
    }
}