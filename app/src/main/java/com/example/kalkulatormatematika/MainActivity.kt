package com.example.kalkulatormatematika

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import java.util.Locale
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFFF9800),
            background = Color(0xFFF5F5F5)
        ),
        content = content
    )
}

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var currentInput by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var firstOperand by remember { mutableDoubleStateOf(0.0) }

    // Fungsi untuk menghitung ukuran font dinamis berdasarkan panjang teks
    fun getDisplayFontSize(text: String): TextUnit {
        return when {
            text.length <= 8 -> 64.sp
            text.length <= 12 -> 48.sp
            text.length <= 16 -> 36.sp
            text.length <= 20 -> 28.sp
            else -> 22.sp
        }
    }

    fun appendToInput(value: String) {
        currentInput += value
        display = currentInput
    }

    fun setOperator(op: String) {
        if (currentInput.isNotEmpty()) {
            firstOperand = currentInput.toDoubleOrNull() ?: 0.0
            operator = op
            currentInput = ""
        }
    }

    fun formatResult(result: Double): String {
        return if (result.isNaN() || result.isInfinite()) {
            "Error"
        } else if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            String.format(Locale.US, "%.8f", result).trimEnd('0').trimEnd('.')
        }
    }

    fun calculate() {
        if (currentInput.isEmpty() || operator.isEmpty()) return
        val secondOperand = currentInput.toDoubleOrNull() ?: 0.0
        val result = when (operator) {
            "+" -> firstOperand + secondOperand
            "-" -> firstOperand - secondOperand
            "×" -> firstOperand * secondOperand
            "÷" -> if (secondOperand != 0.0) firstOperand / secondOperand else Double.NaN
            "^" -> firstOperand.pow(secondOperand)
            else -> 0.0
        }
        display = formatResult(result)
        currentInput = display
        operator = ""
    }

    fun clear() {
        currentInput = ""
        operator = ""
        firstOperand = 0.0
        display = "0"
    }

    fun factorial(n: Int): Long {
        if (n < 0) return 0
        if (n == 0 || n == 1) return 1
        var result = 1L
        for (i in 2..n) {
            result *= i
        }
        return result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calculator",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Display dengan ukuran font dinamis
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = display,
                fontSize = getDisplayFontSize(display),
                color = Color.Black,
                textAlign = TextAlign.End,
                maxLines = 2
            )
        }

        // Button Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Row 1 - Fungsi Matematika
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MathButton("√") {
                    if (currentInput.isNotEmpty()) {
                        val value = currentInput.toDoubleOrNull() ?: 0.0
                        currentInput = formatResult(sqrt(value))
                        display = currentInput
                    }
                }
                MathButton("x²") {
                    if (currentInput.isNotEmpty()) {
                        val value = currentInput.toDoubleOrNull() ?: 0.0
                        currentInput = formatResult(value.pow(2))
                        display = currentInput
                    }
                }
                MathButton("x^y") { setOperator("^") }
                MathButton("x!") {
                    if (currentInput.isNotEmpty()) {
                        val value = currentInput.toIntOrNull() ?: 0
                        if (value <= 20) { // Batasi faktorial untuk menghindari overflow
                            currentInput = factorial(value).toString()
                            display = currentInput
                        } else {
                            display = "Error"
                        }
                    }
                }
                MathButton("π") {
                    if (currentInput.isEmpty() || currentInput == "0") {
                        currentInput = formatResult(Math.PI)
                    } else {
                        val value = currentInput.toDoubleOrNull() ?: 0.0
                        currentInput = formatResult(value * Math.PI)
                    }
                    display = currentInput
                }
            }

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OperatorButton("AC") { clear() }
                OperatorButton("⌫") {
                    if (currentInput.isNotEmpty()) {
                        currentInput = currentInput.dropLast(1)
                        display = currentInput.ifEmpty { "0" }
                    }
                }
                OperatorButton("%") {
                    if (currentInput.isNotEmpty()) {
                        val value = currentInput.toDoubleOrNull() ?: 0.0
                        currentInput = formatResult(value / 100)
                        display = currentInput
                    }
                }
                OperatorButton("÷") { setOperator("÷") }
            }

            // Row 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton("7") { appendToInput("7") }
                NumberButton("8") { appendToInput("8") }
                NumberButton("9") { appendToInput("9") }
                OperatorButton("×") { setOperator("×") }
            }

            // Row 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton("4") { appendToInput("4") }
                NumberButton("5") { appendToInput("5") }
                NumberButton("6") { appendToInput("6") }
                OperatorButton("-") { setOperator("-") }
            }

            // Row 5
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton("1") { appendToInput("1") }
                NumberButton("2") { appendToInput("2") }
                NumberButton("3") { appendToInput("3") }
                OperatorButton("+") { setOperator("+") }
            }

            // Row 6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton("0") { appendToInput("0") }
                NumberButton(".") {
                    if (!currentInput.contains(".")) {
                        if (currentInput.isEmpty()) currentInput = "0"
                        appendToInput(".")
                    }
                }
                EqualsButton("=") { calculate() }
            }
        }
    }
}

@Composable
fun RowScope.NumberButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(70.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(text = text, fontSize = 24.sp)
    }
}

@Composable
fun RowScope.OperatorButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(70.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFFFF9800)
        )
    ) {
        Text(text = text, fontSize = 24.sp)
    }
}

@Composable
fun RowScope.EqualsButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(2f)
            .height(70.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF9800),
            contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 28.sp)
    }
}

@Composable
fun RowScope.MathButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(70.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE3F2FD),
            contentColor = Color(0xFF1976D2)
        )
    ) {
        Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}git