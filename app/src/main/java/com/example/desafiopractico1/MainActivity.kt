package com.example.desafiopractico1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAverage = findViewById<ConstraintLayout>(R.id.btnAverage)
        val btnDiscounts = findViewById<ConstraintLayout>(R.id.btnDiscounts)
        val btnCalculator = findViewById<ConstraintLayout>(R.id.btnCalculator)

        btnAverage.setOnClickListener {
            val intent = Intent(this, AverageActivity::class.java)
            startActivity(intent)
        }

        btnDiscounts.setOnClickListener {
            val intent = Intent(this, SalaryActivity::class.java)
            startActivity(intent)
        }

        btnCalculator.setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }
    }
}