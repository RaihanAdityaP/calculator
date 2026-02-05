package com.example.kalkulatormatematika

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    fun appendToInput(value: String) {
        if (currentInput == "0") {
            currentInput = value
        } else {
            currentInput += value
        }
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
            String.format(Locale.US, "%.10f", result).trimEnd('0').trimEnd('.')
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
        if (n < 0 || n > 20) return 0
        if (n == 0 || n == 1) return 1
        var result = 1L
        for (i in 2..n) {
            result *= i
        }
        return result
    }

    // Calculate dynamic font size based on display length
    val displayFontSize = when {
        display.length > 15 -> 28.sp
        display.length > 12 -> 36.sp
        display.length > 9 -> 44.sp
        display.length > 6 -> 52.sp
        else -> 60.sp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp, start = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calculator",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        // Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = display,
                fontSize = displayFontSize,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.End,
                maxLines = 2,
                lineHeight = displayFontSize
            )
        }

        // Scientific Functions Row 1
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScientificButton("x^y", Modifier.weight(1f)) { setOperator("^") }
            ScientificButton("√x", Modifier.weight(1f)) {
                if (currentInput.isNotEmpty()) {
                    val value = currentInput.toDoubleOrNull() ?: 0.0
                    currentInput = formatResult(sqrt(value))
                    display = currentInput
                }
            }
            ScientificButton("x!", Modifier.weight(1f)) {
                if (currentInput.isNotEmpty()) {
                    val value = currentInput.toIntOrNull() ?: 0
                    val result = factorial(value)
                    currentInput = if (result > 0) result.toString() else "Error"
                    display = currentInput
                }
            }
            ScientificButton("π", Modifier.weight(1f)) {
                currentInput = formatResult(Math.PI)
                display = currentInput
            }
            OperatorButton("÷", Modifier.weight(1f)) { setOperator("÷") }
        }

        // Scientific Functions Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScientificButton("sin", Modifier.weight(1f)) {
                if (currentInput.isNotEmpty()) {
                    val value = currentInput.toDoubleOrNull() ?: 0.0
                    currentInput = formatResult(sin(Math.toRadians(value)))
                    display = currentInput
                }
            }
            ScientificButton("cos", Modifier.weight(1f)) {
                if (currentInput.isNotEmpty()) {
                    val value = currentInput.toDoubleOrNull() ?: 0.0
                    currentInput = formatResult(cos(Math.toRadians(value)))
                    display = currentInput
                }
            }
            ScientificButton("tan", Modifier.weight(1f)) {
                if (currentInput.isNotEmpty()) {
                    val value = currentInput.toDoubleOrNull() ?: 0.0
                    currentInput = formatResult(tan(Math.toRadians(value)))
                    display = currentInput
                }
            }
            OperatorButton("AC", Modifier.weight(1f)) { clear() }
            OperatorButton("⌫", Modifier.weight(1f)) {
                if (currentInput.isNotEmpty()) {
                    currentInput = currentInput.dropLast(1)
                    display = currentInput.ifEmpty { "0" }
                }
            }
        }

        // Number pad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 7-8-9-×
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberButton("7", Modifier.weight(1f)) { appendToInput("7") }
                NumberButton("8", Modifier.weight(1f)) { appendToInput("8") }
                NumberButton("9", Modifier.weight(1f)) { appendToInput("9") }
                OperatorButton("×", Modifier.weight(1f)) { setOperator("×") }
            }

            // Row 4-5-6--
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberButton("4", Modifier.weight(1f)) { appendToInput("4") }
                NumberButton("5", Modifier.weight(1f)) { appendToInput("5") }
                NumberButton("6", Modifier.weight(1f)) { appendToInput("6") }
                OperatorButton("-", Modifier.weight(1f)) { setOperator("-") }
            }

            // Row 1-2-3-+
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberButton("1", Modifier.weight(1f)) { appendToInput("1") }
                NumberButton("2", Modifier.weight(1f)) { appendToInput("2") }
                NumberButton("3", Modifier.weight(1f)) { appendToInput("3") }
                OperatorButton("+", Modifier.weight(1f)) { setOperator("+") }
            }

            // Row 0-.-=
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberButton("0", Modifier.weight(1f)) { appendToInput("0") }
                NumberButton(".", Modifier.weight(1f)) {
                    if (!currentInput.contains(".")) {
                        if (currentInput.isEmpty()) currentInput = "0"
                        appendToInput(".")
                    }
                }
                EqualsButton("=", Modifier.weight(2f)) { calculate() }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun NumberButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun OperatorButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFFFF9800)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun ScientificButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF666666)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EqualsButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF9800),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 6.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal
        )
    }
}