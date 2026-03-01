package com.example.desafiopractico1

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow
import kotlin.math.sqrt
import java.text.DecimalFormat

class CalculatorActivity : AppCompatActivity() {

    private var input        = ""
    private var num1         = 0.0
    private var operation    = ""
    private var newInput = true

    private lateinit var tvDisplay:   TextView
    private lateinit var tvExpression: TextView
    private lateinit var tvRecord: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        tvDisplay   = findViewById(R.id.tvDisplay)
        tvExpression = findViewById(R.id.tvExpression)
        tvRecord = findViewById(R.id.tvRecord)

        val btnVolver = findViewById<TextView>(R.id.btnBackCalc)
        btnVolver.setOnClickListener { finish() }

        // Números y punto
        mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9", R.id.btnDot to "."
        ).forEach { (id, v) ->
            findViewById<TextView>(id).setOnClickListener { haptic(it as TextView); onDigit(v) }
        }

        // Operadores
        mapOf(
            R.id.btnSum  to "+", R.id.btnMinus to "−",
            R.id.btnMult  to "×", R.id.btnDiv   to "÷",
            R.id.btnExp   to "^"
        ).forEach { (id, op) ->
            findViewById<TextView>(id).setOnClickListener { haptic(it as TextView); onOperator(op) }
        }

        // Acciones especiales
        findViewById<TextView>(R.id.btnAC).setOnClickListener         { haptic(it as TextView); onAC() }
        findViewById<TextView>(R.id.btnDelete).setOnClickListener     { haptic(it as TextView); onDelete() }
        findViewById<TextView>(R.id.btnEqual).setOnClickListener      { haptic(it as TextView); onEqual() }
        findViewById<TextView>(R.id.btnRoot).setOnClickListener       { haptic(it as TextView); onRoot() }
        findViewById<TextView>(R.id.btnPlusMinus).setOnClickListener  { haptic(it as TextView); onPlusMinus() }
        findViewById<TextView>(R.id.btnPercentage).setOnClickListener { haptic(it as TextView); onPercentage() }
    }

    private fun onDigit(v: String) {
        if (newInput) {
            input = if (v == ".") "0." else v
            newInput = false
        } else {
            if (v == "." && input.contains(".")) return
            if (v == "0" && input == "0") return
            input = if (input == "0" && v != ".") v else input + v
        }
        updateDisplay(input)
    }

    private fun onOperator(op: String) {
        val current = input.toDoubleOrNull() ?: num1
        if (operation.isNotEmpty() && !newInput) {
            val r = calculate(num1, current, operation) ?: return
            num1 = r
            updateDisplay(formatResult(num1))
        } else {
            num1 = current
        }
        operation    = op
        newInput = true
        tvExpression.text = "${formatResult(num1)} $op"
    }

    private fun onEqual() {
        if (operation.isEmpty()) return
        val num2 = input.toDoubleOrNull() ?: return
        val expr = "${formatResult(num1)} $operation ${formatResult(num2)}"
        val r    = calculate(num1, num2, operation) ?: return

        tvRecord.text = "$expr ="
        tvExpression.text = ""
        updateDisplay(formatResult(r))
        num1         = r
        input        = formatResult(r)
        operation    = ""
        newInput = true
    }

    private fun onRoot() {
        val v = input.toDoubleOrNull() ?: num1
        if (v < 0) { showError("√ de número negativo"); return }
        val r = sqrt(v)
        tvRecord.text = "√(${formatResult(v)}) ="
        tvExpression.text = ""
        updateDisplay(formatResult(r))
        num1 = r; input = formatResult(r); newInput = true
    }

    private fun onPlusMinus() {
        val v = input.toDoubleOrNull() ?: return
        input = formatResult(-v); updateDisplay(input); newInput = false
    }

    private fun onPercentage() {
        val v = input.toDoubleOrNull() ?: return
        input = formatResult(v / 100.0); updateDisplay(input); newInput = false
    }

    private fun onAC() {
        input = ""; num1 = 0.0; operation = ""; newInput = true
        tvDisplay.text = "0"; tvDisplay.textSize = 72f
        tvExpression.text = ""; tvRecord.text = ""
    }

    private fun onDelete() {
        if (newInput) { onAC(); return }
        input = input.dropLast(1)
        if (input.isEmpty() || input == "-") { input = ""; newInput = true; updateDisplay("0") }
        else updateDisplay(input)
    }

    // ══════════════════ HELPERS ══════════════════

    private fun calculate(a: Double, b: Double, op: String): Double? = when (op) {
        "+"  -> a + b
        "−"  -> a - b
        "×"  -> a * b
        "÷"  -> if (b == 0.0) { showError("División entre 0"); null } else a / b
        "^"  -> a.pow(b)
        else -> b
    }

    private fun showError(msg: String) {
        tvExpression.text = "⚠ $msg"
        updateDisplay("Error")
        operation = ""; newInput = true
    }

    private fun updateDisplay(value: String) {
        tvDisplay.text = value
        tvDisplay.textSize = when {
            value.length > 12 -> 36f
            value.length > 9  -> 48f
            value.length > 6  -> 60f
            else              -> 72f
        }
    }

    private fun formatResult(n: Double): String {
        if (n.isInfinite() || n.isNaN()) return "Error"
        return if (n == n.toLong().toDouble() && kotlin.math.abs(n) < 1e12)
            n.toLong().toString()
        else DecimalFormat("0.##########").format(n)
    }

    private fun haptic(v: TextView) =
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
}
