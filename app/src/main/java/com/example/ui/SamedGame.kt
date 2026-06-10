package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatRepository
import com.example.data.GameScoreEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamedGameScreen(
    repository: ChatRepository,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var leaderboard by remember { mutableStateOf(listOf<GameScoreEntity>()) }

    // Fetch leaderboard
    LaunchedEffect(Unit) {
        repository.getTopScores().collect {
            leaderboard = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("لعبة صامد 2D 🎮 - الصمود والثبات", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("game_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A0A))
            )
        },
        containerColor = Color(0xFF0A0A0A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isPlaying && !isGameOver) {
                // Intro Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Games,
                        contentDescription = "Game Logo",
                        tint = Color(0xFF2E5E44),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "صـامـد (Samed) - أوفـلايـن",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "أنت تمشي وسط مدينة مهدمة لتجمع هدايا الصمود.\n" +
                                "اجمع: مفتاح العودة 🔑، أغصان زيتون 🌿، والعملة 🪙.\n" +
                                "تجنب: القذائف والكتل المتساقطة 💥.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFB0B0B0),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            score = 0
                            isGameOver = false
                            isPlaying = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A2A)),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(56.dp)
                            .testTag("start_game_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ابدأ الصمود الآن", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    Text("🏆 لوحة شرف الصامدين الأسبوعية 🏆", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(leaderboard) { item ->
                            val isHero = item.userId.contains("المجاهد")
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isHero) Color(0xFF1A3A2A) else Color(0xFF1E1E1E)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.userId, color = Color.White, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(item.localBadgeType, color = Color(0xFFD4AF37), fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
                                        Text("${item.score} ن", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (isGameOver) {
                // Game Over Screen
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "انتهى التحدي والرباط! 🤝",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF9E2A2B),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "حققت نتيجة: $score نقطة",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val badge = when {
                        score >= 1000 -> "وسام الصمود الذهبي 🎖️"
                        score >= 500 -> "وسام الصمود الفضي 🥈"
                        else -> "وسام الصمود البرونزي 🥉"
                    }
                    Text(
                        "الحالة: لقد حصلت على $badge",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFD4AF37),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            isPlaying = true
                            isGameOver = false
                            score = 0
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A2A)),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(56.dp)
                            .testTag("retry_game_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("العب مجدداً", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            isPlaying = false
                            isGameOver = false
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(56.dp)
                    ) {
                        Icon(Icons.Default.Leaderboard, contentDescription = "Leaderboard", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("لوحة الصامدين", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else {
                // Playing Screen (Compose Game Engine)
                ActiveGameEngine(
                    score = score,
                    onScoreChange = { score = it },
                    onGameOver = {
                        isPlaying = false
                        isGameOver = true
                        coroutineScope.launch {
                            repository.saveGameScore(score)
                        }
                    }
                )
            }
        }
    }
}

// 2D Game Components State
data class GameEntity(
    var x: Float,
    var y: Float,
    val type: EntityType, // FALLING_BOMB, RECOVERABLE_GOLD, RECOVERABLE_KEY, RECOVERABLE_OLIVE
    val speed: Float,
    val size: Float = 40f
)

enum class EntityType {
    BOMB, // Red dangerous object
    COIN, // Golden coin
    KEY,  // White Key of Return
    OLIVE // Green branch of olive
}

@Composable
fun ActiveGameEngine(
    score: Int,
    onScoreChange: (Int) -> Unit,
    onGameOver: () -> Unit
) {
    var playerX by remember { mutableStateOf(0.5f) } // normalized 0.0f to 1.0f
    var entities by remember { mutableStateOf(listOf<GameEntity>()) }
    val density = LocalDensity.current

    // Game loops
    LaunchedEffect(Unit) {
        var elapsed = 0L
        while (true) {
            delay(16) // ~60fps
            elapsed += 16

            // Spawn entity every 1.5s
            if (elapsed % 1000 == 0L || entities.isEmpty() && Random.nextFloat() < 0.05f) {
                val randVal = Random.nextFloat()
                val type = when {
                    randVal < 0.45f -> EntityType.BOMB
                    randVal < 0.70f -> EntityType.COIN
                    randVal < 0.85f -> EntityType.KEY
                    else -> EntityType.OLIVE
                }
                entities = entities + GameEntity(
                    x = Random.nextFloat(),
                    y = -0.1f,
                    type = type,
                    speed = 0.005f + (score * 0.00002f) + Random.nextFloat() * 0.005f
                )
            }

            // Update entities position
            entities = entities.map { it.copy(y = it.y + it.speed) }

            // Collisions detection
            val safeEntities = mutableListOf<GameEntity>()
            for (ent in entities) {
                if (ent.y >= 1.0f) {
                    // Fell past screen
                    if (ent.type != EntityType.BOMB) {
                        // missed positive item, no penalty or light penalty
                    }
                } else if (ent.y >= 0.8f && ent.y <= 0.95f && kotlin.math.abs(ent.x - playerX) < 0.12f) {
                    // Collision with hero!
                    when (ent.type) {
                        EntityType.BOMB -> {
                            onGameOver()
                            return@LaunchedEffect
                        }
                        EntityType.COIN -> onScoreChange(score + 10)
                        EntityType.KEY -> onScoreChange(score + 30)
                        EntityType.OLIVE -> onScoreChange(score + 50)
                    }
                } else {
                    safeEntities.add(ent)
                }
            }
            entities = safeEntities

            if (elapsed > 1000000L) elapsed = 0L
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF030A05), Color(0xFF0C140E))
                )
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val dragFraction = dragAmount.x / size.width
                    playerX = (playerX + dragFraction).coerceIn(0.05f, 0.95f)
                }
            }
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw background ruined city skyline (generative silhouettes)
            drawRect(
                color = Color(0xFF1E1E1E),
                topLeft = Offset(0f, height * 0.75f),
                size = Size(width, height * 0.25f)
            )

            // Draw Rubble silhouettes
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, height * 0.85f)
                    lineTo(width * 0.2f, height * 0.78f)
                    lineTo(width * 0.35f, height * 0.83f)
                    lineTo(width * 0.5f, height * 0.72f)
                    lineTo(width * 0.75f, height * 0.84f)
                    lineTo(width, height * 0.75f)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                },
                color = Color(0xFF131A15)
            )

            // Draw Entities falling down
            for (ent in entities) {
                val entXDisp = ent.x * width
                val entYDisp = ent.y * height
                when (ent.type) {
                    EntityType.BOMB -> {
                        // Red projectile threat
                        drawCircle(
                            color = Color(0xFF9E2A2B),
                            radius = 24f,
                            center = Offset(entXDisp, entYDisp)
                        )
                        // Spark
                        drawCircle(
                            color = Color(0xFFD4AF37),
                            radius = 8f,
                            center = Offset(entXDisp - 8f, entYDisp - 18f)
                        )
                    }
                    EntityType.COIN -> {
                        // Gold coin
                        drawCircle(
                            color = Color(0xFFD4AF37),
                            radius = 20f,
                            center = Offset(entXDisp, entYDisp)
                        )
                        drawCircle(
                            color = Color(0xFFFFFFFF),
                            radius = 12f,
                            center = Offset(entXDisp, entYDisp)
                        )
                    }
                    EntityType.KEY -> {
                        // Key of Return
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(entXDisp - 8f, entYDisp - 24f),
                            size = Size(16f, 40f)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 16f,
                            center = Offset(entXDisp, entYDisp - 16f)
                        )
                        // teeth
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(entXDisp + 4f, entYDisp + 8f),
                            size = Size(14f, 8f)
                        )
                    }
                    EntityType.OLIVE -> {
                        // Olive branch (Green leaf)
                        drawOval(
                            color = Color(0xFF2E5E44),
                            topLeft = Offset(entXDisp - 14f, entYDisp - 24f),
                            size = Size(28f, 48f)
                        )
                        // details
                        drawOval(
                            color = Color(0xFF214230),
                            topLeft = Offset(entXDisp - 8f, entYDisp - 14f),
                            size = Size(16f, 28f)
                        )
                    }
                }
            }

            // Draw player hero representation (المجاهد الصامد) at bottom 85%
            val playerSize = 70f
            val px = playerX * width
            val py = height * 0.86f

            // Vest/Coat (Military Olive Green)
            drawRect(
                color = Color(0xFF1A3A2A),
                topLeft = Offset(px - playerSize / 2f, py - 4f),
                size = Size(playerSize, playerSize / 1.2f)
            )

            // Shoulder badges
            drawRect(
                color = Color(0xFFC19A6B),
                topLeft = Offset(px - playerSize / 2f, py - 4f),
                size = Size(16f, 8f)
            )
            drawRect(
                color = Color(0xFFC19A6B),
                topLeft = Offset(px + playerSize / 2f - 16f, py - 4f),
                size = Size(16f, 8f)
            )

            // Shield / Palestinian Motif on chest
            drawRect(
                color = Color(0xFF1E1E1E),
                topLeft = Offset(px - 14f, py + 12f),
                size = Size(28f, 32f)
            )
            // Draw Palestinian flag stripes on shield
            drawRect(color = Color.Black, topLeft = Offset(px - 12f, py + 14f), size = Size(24f, 6f))
            drawRect(color = Color.White, topLeft = Offset(px - 12f, py + 20f), size = Size(24f, 6f))
            drawRect(color = Color(0xFF2E5E44), topLeft = Offset(px - 12f, py + 26f), size = Size(24f, 6f))
            // Triangle
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(px - 12f, py + 14f)
                    lineTo(px, py + 23f)
                    lineTo(px - 12f, py + 32f)
                    close()
                },
                color = Color(0xFF9E2A2B)
            )

            // Head / Helmet (charcoal)
            drawCircle(
                color = Color(0xFF2C3E35),
                radius = 24f,
                center = Offset(px, py - 20f)
            )
            // Headband "كوفية" wraps
            drawRect(
                color = Color(0xFF9E2A2B),
                topLeft = Offset(px - 25f, py - 22f),
                size = Size(50f, 6f)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(px - 25f, py - 28f),
                size = Size(50f, 3f)
            )

            // Eyes / Goggles block
            drawRect(
                color = Color(0xFF111111),
                topLeft = Offset(px - 16f, py - 18f),
                size = Size(32f, 8f)
            )
        }

        // Live Scoring and tutorial HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xCC0A0A0A)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🏆 النقاط: $score",
                    color = Color(0xFFD4AF37),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Text(
                "👈 اسحب يميناً ويساراً للحركة 👉",
                color = Color.White,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .background(Color(0x771E1E1E), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            )
        }
    }
}
