package com.example.advancedcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.advancedcalculator.ui.theme.AdvancedCalculatorTheme
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            // Shimmer animation background setup
            val infiniteTransition = rememberInfiniteTransition()
            val animatedOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            val shimmerBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFBBDEFB), // Light Blue
                    Color(0xFF64B5F6), // Medium Blue
                    Color(0xFF1E88E5)  // Dark Blue
                ),
                startY = animatedOffset,
                endY = animatedOffset + 300f
            )

            AdvancedCalculatorTheme(darkTheme = isDarkMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(shimmerBrush)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        ThemeToggle(isDarkMode = isDarkMode, onToggle = { isDarkMode = it })
                        CalculatorUI()
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeToggle(isDarkMode: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(checked = isDarkMode, onCheckedChange = onToggle)
    }
}

fun evaluateExpression(expression: String): String {
    return try {
        val sinFunction = object : Function("sin", 1) {
            override fun apply(vararg args: Double): Double {
                return sin(Math.toRadians(args[0]))
            }
        }
        val cosFunction = object : Function("cos", 1) {
            override fun apply(vararg args: Double): Double {
                return cos(Math.toRadians(args[0]))
            }
        }
        val tanFunction = object : Function("tan", 1) {
            override fun apply(vararg args: Double): Double {
                return tan(Math.toRadians(args[0]))
            }
        }
        val sqrtFunction = object : Function("sqrt", 1) {
            override fun apply(vararg args: Double): Double {
                return sqrt(args[0])
            }
        }
        val logFunction = object : Function("log", 1) {
            override fun apply(vararg args: Double): Double {
                return log10(args[0])
            }
        }

        val expressionBuilder = ExpressionBuilder(expression)
            .function(sinFunction)
            .function(cosFunction)
            .function(tanFunction)
            .function(sqrtFunction)
            .function(logFunction)
            .build()

        val result = expressionBuilder.evaluate()
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

@Composable
fun CalculatorUI() {
    var input by remember { mutableStateOf("0") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = input,
                        fontSize = 34.sp,
                        lineHeight = 42.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .padding(end = 16.dp)
                    )
                }
            }

            val buttonModifier = Modifier
                .weight(1f)
                .padding(4.dp)
                .height(60.dp)

            val buttons = listOf(
                listOf("sin", "cos", "tan", "sqrt"),
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("0", ".", "+", "^"),
                listOf("C", "(", ")", "="),
                listOf("log")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { label ->
                        NeonButton(
                            label = label,
                            onClick = {
                                input = when (label) {
                                    "=" -> evaluateExpression(input)
                                    "C" -> "0"
                                    else -> if (input == "0") label else input + label
                                }
                            },
                            modifier = buttonModifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NeonButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        targetValue = if (isPressed) Color.Black else Color(0xFF000000),
        label = "neon-color"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(animatedColor, RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color.Cyan, Color.Magenta)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                    isPressed = false
                }
            )
    ) {
        Text(text = label, fontSize = 20.sp, color = Color.Cyan)
    }
}
