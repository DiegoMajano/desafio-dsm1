package com.example.desafiopractico1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.DecimalFormat

class AverageActivity : AppCompatActivity() {

    data class Note(val editText: EditText, val peso: Double, val name: String)

    private lateinit var notes: List<Note>
    private lateinit var tvAverage: TextView
    private lateinit var tvState: TextView
    private lateinit var tvNameResult: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var cardResult: androidx.cardview.widget.CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_average)

        val etName    = findViewById<EditText>(R.id.etName)
        val btnCalc     = findViewById<Button>(R.id.btnCalculate)
        val btnClear  = findViewById<Button>(R.id.btnClear)
        val btnBack   = findViewById<TextView>(R.id.btnBack)

        tvAverage      = findViewById(R.id.tvAverage)
        tvState        = findViewById(R.id.tvState)
        tvNameResult  = findViewById(R.id.tvNameResult)
        progressBar     = findViewById(R.id.progressBar)
        cardResult   = findViewById(R.id.cardResult)

        notes = listOf(
            Note(findViewById(R.id.etNota1), 0.15, "Tarea"),
            Note(findViewById(R.id.etNota2), 0.15, "Participación"),
            Note(findViewById(R.id.etNota3), 0.20, "Laboratorio"),
            Note(findViewById(R.id.etNota4), 0.25, "Parcial"),
            Note(findViewById(R.id.etNota5), 0.25, "Final")
        )

        // Actualizar barra de progreso en tiempo real
        notes.forEach { nota ->
            nota.editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { updateProgress() }
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
                override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            })
        }

        btnBack.setOnClickListener { finish() }

        btnClear.setOnClickListener {
            etName.text.clear()
            notes.forEach { it.editText.text.clear(); it.editText.error = null }
            cardResult.alpha = 0f
            progressBar.progress = 0
        }

        btnCalc.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Ingresa el nombre del estudiante"
                etName.requestFocus()
                return@setOnClickListener
            }

            val values = mutableListOf<Double>()
            var hayError = false

            for (nota in notes) {
                val texto = nota.editText.text.toString().trim()
                if (texto.isEmpty()) {
                    nota.editText.error = "Requerido"
                    hayError = true
                    continue
                }
                val v = texto.toDoubleOrNull()
                if (v == null || v < 0 || v > 10) {
                    nota.editText.error = "0 – 10"
                    hayError = true
                    continue
                }
                nota.editText.error = null
                values.add(v)
            }

            if (hayError) return@setOnClickListener

            val average = calculateAverage(values)
            val df = DecimalFormat("0.00")
            val approved = average >= 6.0

            tvNameResult.text = name
            tvAverage.text = df.format(average)
            tvState.text = if (approved) "✓  APROBADO" else "✗  REPROBADO"
            tvState.setTextColor(
                ContextCompat.getColor(this,
                    if (approved) R.color.green_approved else R.color.red_failed)
            )

            // Animar la card de resultado
            cardResult.alpha = 0f
            cardResult.animate().alpha(1f).setDuration(400).start()
            progressBar.progress = (average * 10).toInt()
        }
    }

    private fun calculateAverage(values: List<Double>): Double {
        var sum = 0.0
        notes.forEachIndexed { i, note -> sum += values[i] * note.peso }
        return sum
    }

    private fun updateProgress() {
        val values = notes.mapNotNull { it.editText.text.toString().toDoubleOrNull() }
        if (values.size == notes.size) {
            val p = calculateAverage(values)
            progressBar.progress = (p * 10).toInt()
        }
    }
}
