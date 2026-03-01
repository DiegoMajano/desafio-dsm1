package com.example.desafiopractico1

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class SalaryActivity : AppCompatActivity() {

    private lateinit var tvNameResult: TextView
    private lateinit var tvSalaryBase: TextView
    private lateinit var tvDiscAFP: TextView
    private lateinit var tvDiscISSS: TextView
    private lateinit var tvDiscRent: TextView
    private lateinit var tvTotalDisc: TextView
    private lateinit var tvNetSalary: TextView
    private lateinit var resultCard: CardView
    private lateinit var progressDesc: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary)

        val etName   = findViewById<EditText>(R.id.etEmpName)
        val etSalary  = findViewById<EditText>(R.id.etSalaryBase)
        val btnCalc    = findViewById<MaterialButton>(R.id.btnSalaryCalculate)
        val btnClear = findViewById<MaterialButton>(R.id.btnCleanSalary)
        val btnBack  = findViewById<TextView>(R.id.btnBackSalary)

        tvNameResult = findViewById(R.id.tvResultNameSal)
        tvSalaryBase  = findViewById(R.id.tvResSalarioBase)
        tvDiscAFP      = findViewById(R.id.tvResAFP)
        tvDiscISSS     = findViewById(R.id.tvResISSS)
        tvDiscRent    = findViewById(R.id.tvResRenta)
        tvTotalDisc    = findViewById(R.id.tvResTotalDesc)
        tvNetSalary  = findViewById(R.id.tvResNetSalary)
        resultCard  = findViewById(R.id.resultCardSal)
        progressDesc   = findViewById(R.id.progressDiscounts)

        btnBack.setOnClickListener { finish() }

        btnClear.setOnClickListener {
            etName.text.clear()
            etSalary.text.clear()
            etName.error = null
            etSalary.error = null
            resultCard.alpha = 0f
            progressDesc.progress = 0
        }

        btnCalc.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Ingresa el nombre del empleado"
                etName.requestFocus()
                return@setOnClickListener
            }

            val salText = etSalary.text.toString().trim()
            if (salText.isEmpty()) {
                etSalary.error = "Ingresa el salario base"
                etSalary.requestFocus()
                return@setOnClickListener
            }

            val salary = salText.toDoubleOrNull()
            if (salary == null || salary <= 0) {
                etSalary.error = "Debe ser un valor positivo"
                return@setOnClickListener
            }

            etName.error = null
            etSalary.error = null

            val afp   = calculateAFP(salary)
            val isss  = calculateISSS(salary)
            val rent = calculateRent(salary)
            val totalDisc = afp + isss + rent
            val net  = salary - totalDisc
            val percDisc = ((totalDisc / salary) * 100).toInt().coerceIn(0, 100)

            val df = DecimalFormat("$#,##0.00")

            tvNameResult.text = name
            tvSalaryBase.text  = df.format(salary)
            tvDiscAFP.text      = df.format(afp)
            tvDiscISSS.text     = df.format(isss)
            tvDiscRent.text    = df.format(rent)
            tvTotalDisc.text    = df.format(totalDisc)
            tvNetSalary.text  = df.format(net)

            progressDesc.progress = percDisc

            resultCard.alpha = 0f
            resultCard.animate().alpha(1f).setDuration(400).start()
        }
    }

    private fun calculateAFP(salary: Double) = salary * 0.0725

    private fun calculateISSS(salary: Double) = salary * 0.03

    private fun calculateRent(salary: Double): Double {
        return when {
            salary <= 472.00   -> 0.0
            salary <= 895.24   -> (salary - 472.00)  * 0.10 + 17.67
            salary <= 2038.10  -> (salary - 895.24)  * 0.20 + 60.00
            else                -> (salary - 2038.10) * 0.30 + 288.57
        }
    }
}
