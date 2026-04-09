package com.example.myapplication.ui.theme

import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import android.content.Context
import android.graphics.*
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val context = LocalContext.current

    // Создаем аниматор с режимом REVERSE (туда-обратно)
    val animator = remember {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 6000 // 6 секунд в одну сторону
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE // 🔄 КЛЮЧЕВОЙ МОМЕНТ - движение туда-обратно
            interpolator = AccelerateDecelerateInterpolator() // Плавное ускорение и замедление
        }
    }

    val gradientView = remember {
        android.view.View(context).apply {
            // Цвета градиента
            val colorStart = Color.parseColor("#FF6B6B")  // Красный
            val colorMiddle = Color.parseColor("#4ECDC4") // Бирюзовый
            val colorEnd = Color.parseColor("#FFE66D")    // Желтый

            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(colorStart, colorMiddle, colorEnd)
            ).apply {
                gradientType = GradientDrawable.LINEAR_GRADIENT
            }

            background = gradientDrawable

            // Анимация смены цветов
            animator.addUpdateListener { animation ->
                val fraction = animation.animatedFraction

                // При движении обратно fraction уменьшается от 1 до 0
                val newColors = when {
                    fraction <= 0.5f -> {
                        // Первая половина: от начальных до средних цветов
                        val f = fraction * 2
                        intArrayOf(
                            interpolateColor(colorStart, colorMiddle, f),
                            interpolateColor(colorMiddle, colorEnd, f),
                            interpolateColor(colorEnd, colorStart, f)
                        )
                    }
                    else -> {
                        // Вторая половина: от средних обратно к начальным
                        val f = (1 - fraction) * 2
                        intArrayOf(
                            interpolateColor(colorStart, colorMiddle, f),
                            interpolateColor(colorMiddle, colorEnd, f),
                            interpolateColor(colorEnd, colorStart, f)
                        )
                    }
                }

                gradientDrawable.colors = newColors
            }
        }
    }

    DisposableEffect(Unit) {
        animator.start()
        onDispose {
            animator.cancel()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { gradientView },
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}

private fun interpolateColor(startColor: Int, endColor: Int, fraction: Float): Int {
    val startA = (startColor shr 24) and 0xFF
    val startR = (startColor shr 16) and 0xFF
    val startG = (startColor shr 8) and 0xFF
    val startB = startColor and 0xFF

    val endA = (endColor shr 24) and 0xFF
    val endR = (endColor shr 16) and 0xFF
    val endG = (endColor shr 8) and 0xFF
    val endB = endColor and 0xFF

    return ((startA + (endA - startA) * fraction).toInt() shl 24) or
            ((startR + (endR - startR) * fraction).toInt() shl 16) or
            ((startG + (endG - startG) * fraction).toInt() shl 8) or
            ((startB + (endB - startB) * fraction).toInt())
}

@Composable
fun AnimatedMoleculesBackground(
    modifier: Modifier = Modifier,
    moleculeCount: Int = 30, // Количество молекул
    content: @Composable () -> Unit = {}
) {
    val context = LocalContext.current

    val moleculeView = remember {
        MoleculeView(context, moleculeCount)
    }

    DisposableEffect(Unit) {
        moleculeView.startAnimation()
        onDispose {
            moleculeView.stopAnimation()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { moleculeView },
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}

class MoleculeView(context: Context, private val moleculeCount: Int) : View(context) {
    private val particles = mutableListOf<Particle>()
    private var animator: ValueAnimator? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var width = 0
    private var height = 0

    data class Particle(
        var x: Float,
        var y: Float,
        var radius: Float,
        var speedX: Float,
        var speedY: Float,
        var targetX: Float,
        var targetY: Float
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w
        height = h
        initParticles()
    }

    private fun initParticles() {
        particles.clear()
        for (i in 0 until moleculeCount) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    radius = Random.nextFloat() * 8f + 2f,
                    speedX = (Random.nextFloat() - 0.5f) * 1f,
                    speedY = (Random.nextFloat() - 0.5f) * 1f,
                    targetX = Random.nextFloat() * width,
                    targetY = Random.nextFloat() * height
                )
            )
        }
    }

    fun startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 8000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateParticles()
                invalidate()
            }
            start()
        }
    }

    fun stopAnimation() {
        animator?.cancel()
        animator = null
    }

    private fun updateParticles() {
        for (p in particles) {
            // Плавное движение к цели
            p.x += (p.targetX - p.x) * 0.02f + p.speedX
            p.y += (p.targetY - p.y) * 0.02f + p.speedY

            // Новая цель при приближении
            if (abs(p.x - p.targetX) < 20 && abs(p.y - p.targetY) < 20) {
                p.targetX = Random.nextFloat() * width
                p.targetY = Random.nextFloat() * height
            }

            // Отскок от границ
            p.x = p.x.coerceIn(p.radius, width - p.radius)
            p.y = p.y.coerceIn(p.radius, height - p.radius)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Чистый темный фон
        canvas.drawColor(Color.rgb(10, 10, 25))

        // Рисуем связи
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                val p1 = particles[i]
                val p2 = particles[j]
                val distance = hypot(p1.x - p2.x, p1.y - p2.y)

                if (distance < 150) {
                    val alpha = (255 * (1 - distance / 150)).toInt()
                    paint.color = Color.argb(alpha / 3, 80, 150, 255)
                    paint.strokeWidth = 1f
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
                }
            }
        }

        // Рисуем частицы
        for (p in particles) {
            paint.color = Color.argb(200, 100, 180, 255)
            canvas.drawCircle(p.x, p.y, p.radius, paint)

            paint.color = Color.argb(100, 150, 200, 255)
            canvas.drawCircle(p.x, p.y, p.radius * 1.5f, paint)
        }
    }
}

// Добавьте в ваш существующий Theme.kt
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ParticlesBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportZoom(false)
                builtInZoomControls = false
            }
            webViewClient = WebViewClient()

            // HTML строка с частицами (встроенный вариант)
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { 
                            width: 100%; 
                            height: 100%; 
                            overflow: hidden;
                            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
                        }
                        #particles-js {
                            position: absolute;
                            width: 100%;
                            height: 100%;
                        }
                    </style>
                </head>
                <body>
                    <div id="particles-js"></div>
                    <script src="https://cdn.jsdelivr.net/particles.js/2.0.0/particles.min.js"></script>
                    <script>
                        particlesJS('particles-js', {
                            "particles": {
                                "number": { "value": 60, "density": { "enable": true, "value_area": 800 } },
                                "color": { "value": "#ffffff" },
                                "shape": { "type": "circle" },
                                "opacity": { "value": 0.5, "random": true },
                                "size": { "value": 3, "random": true },
                                "line_linked": { "enable": true, "distance": 150, "color": "#ffffff", "opacity": 0.4, "width": 1 },
                                "move": { "enable": true, "speed": 4, "direction": "none", "random": false, "straight": false, "out_mode": "out" }
                            },
                            "interactivity": {
                                "detect_on": "canvas",
                                "events": {
                                    "onhover": { "enable": true, "mode": "repulse" },
                                    "onclick": { "enable": true, "mode": "push" },
                                    "resize": true
                                }
                            },
                            "retina_detect": true
                        });
                    </script>
                </body>
                </html>
            """.trimIndent()

            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.destroy()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}