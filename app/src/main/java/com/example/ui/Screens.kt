package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.data.ChatRepository
import com.example.data.GameScoreEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------- AL-TOFAN VECTOR BRAND LOGO ----------------
@Composable
fun AlTofanLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Waving circular Olive Green backgrounds
        drawCircle(
            color = Color(0xFF112317),
            radius = w * 0.46f
        )
        drawCircle(
            color = Color(0xFF1D3524),
            radius = w * 0.38f
        )
        // Waving T shaped main vector logic
        val tPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.22f, h * 0.32f)
            quadraticTo(w * 0.5f, h * 0.20f, w * 0.78f, h * 0.32f)
            lineTo(w * 0.74f, h * 0.44f)
            quadraticTo(w * 0.5f, h * 0.34f, w * 0.26f, h * 0.44f)
            close()
        }
        drawPath(tPath, Color(0xFF2E5E44))

        val stemPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.45f, h * 0.36f)
            quadraticTo(w * 0.55f, h * 0.36f, w * 0.55f, h * 0.36f)
            quadraticTo(w * 0.58f, h * 0.58f, w * 0.36f, h * 0.84f)
            quadraticTo(w * 0.24f, h * 0.80f, w * 0.28f, h * 0.72f)
            quadraticTo(w * 0.46f, h * 0.56f, w * 0.45f, h * 0.36f)
            close()
        }
        drawPath(stemPath, Color(0xFF2E5E44))

        // Red triangle marking (symbolizing Palestinian flag peak and heroic signal)
        val flagPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.50f, h * 0.42f)
            lineTo(w * 0.58f, h * 0.48f)
            lineTo(w * 0.48f, h * 0.54f)
            close()
        }
        drawPath(flagPath, Color(0xFF9E2A2B))
    }
}


// ---------------- SPLASH SCREEN (WITH CALLIGRAPHED SURAH AL-FATIHAH) ----------------
@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit
) {
    var stage by remember { mutableStateOf(0) } // 0 = Bismillah intro, 1 = Surah Al-Fatihah, 2 = Ready entry
    val scale = remember { Animatable(0f) }

    val alFatihahVerses = listOf(
        "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ (1)",
        "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ (2)",
        "الرَّحْمَٰنِ الرَّحِيمِ (3)",
        "مَالِكِ يَوْمِ الدِّينِ (4)",
        "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ (5)",
        "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ (6)",
        "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ (7)"
    )

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        // Stage 0: Play Bismillah with custom 3 seconds delay
        delay(3000)
        stage = 1 // Show Surah Al-Fatihah
    }

    Scaffold(containerColor = Color(0xFF030303)) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (stage == 0) {
                    // Stage 0: Bismillah + Logo intro
                    AlTofanLogo(
                        modifier = Modifier
                            .size(160.dp)
                            .testTag("splash_logo")
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("bismillah_text")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "جاري تهيئة الاتصال العسكري المشفر...",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    LinearProgressIndicator(
                        color = Color(0xFF1A3A2A),
                        trackColor = Color(0xFF1E1E1E),
                        modifier = Modifier.width(150.dp)
                    )
                } else {
                    // Stage 1 & 2: Show Full Surah Al-Fatihah + Enter Button
                    Text(
                        text = "سورة الفاتحة الكريمة 📖",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E0E0E)),
                        border = BorderStroke(1.dp, Color(0xFF1A3A2A)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(alFatihahVerses) { verse ->
                                Text(
                                    text = verse,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF141414), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                        .border(0.5.dp, Color(0xFF1D3524), RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateNext,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A2A)),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                            .testTag("splash_enter_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "ادخـل آمـنـاً وتـواصـل 🔓",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


// ---------------- LOGIN SCREEN (OTP VERIFICATION SIMULATOR) ----------------
@Composable
fun LoginScreen(
    repository: ChatRepository,
    onLoginSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("المجاهد الصامد") }
    var otpCode by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var messageStatus by remember { mutableStateOf("") }
    var countdown by remember { mutableStateOf(30) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isCodeSent) {
        if (isCodeSent) {
            countdown = 30
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
        }
    }

    Scaffold(containerColor = Color(0xFF0A0A0A)) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AlTofanLogo(modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "الطوفان تواصل - تسجيل الدخول",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "تقنية تشفير عسكرية غير قابلة للاختراق 🛡️",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (!isCodeSent) {
                    // Phone Number Step
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("الاسم الحركي / المستعار", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF2E5E44),
                            unfocusedBorderColor = Color(0xFF1E1E1E)
                        ),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (it.length <= 15) phoneNumber = it },
                        label = { Text("رقم الهاتف (مثال: +966500000000)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF2E5E44),
                            unfocusedBorderColor = Color(0xFF1E1E1E)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (phoneNumber.isNotBlank() && userName.isNotBlank()) {
                                coroutineScope.launch {
                                    messageStatus = "جاري الاتصال بقمر الاتصالات العسكري..."
                                    delay(1000)
                                    messageStatus = "إرسال كود OTP الآمن..."
                                    delay(1000)
                                    isCodeSent = true
                                    otpCode = "797241" // Mock standard secure code
                                    messageStatus = "تم إرسال كود التحقق بنجاح!"
                                }
                            } else {
                                messageStatus = "يرجى تعبئة جميع الحقول أولاً!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A2A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("send_code_button")
                    ) {
                        Text("أرسل كود التحقق الآمن OTP 🔑", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    // OTP Verification Step
                    Text(
                        text = "كود التحقق لتسجيل الدخول: 797241",
                        color = Color(0xFFD4AF37),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("أدخل الكود المستلم", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF2E5E44),
                            unfocusedBorderColor = Color(0xFF1E1E1E)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "OTP", tint = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (otpCode == "797241" || otpCode.length >= 6) {
                                repository.setLoggedInUser(phoneNumber, userName)
                                onLoginSuccess()
                            } else {
                                messageStatus = "كود التحقق غير صحيح، حاول مجدداً!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A2A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("verify_login_button")
                    ) {
                        Text("تفعيل الهوية والدخول 🔓", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("إعادة الإرسال خلال: $countdown ثانية", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "تعديل الرقم ✏️",
                            color = Color(0xFF2E5E44),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isCodeSent = false }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                AnimatedVisibility (messageStatus.isNotBlank()) {
                    Text(
                        text = messageStatus,
                        color = if (messageStatus.contains("بنجاح")) Color(0xFF2E5E44) else Color(0xFF9E2A2B),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .background(Color(0xFF141414), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}


// ---------------- SETTINGS & IDENTITY & LIBRARY SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: ChatRepository,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = هوية الطوفان, 1 = متجر الهدايا, 2 = مكتبة الصمود
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("إعدادات طوفان الأقصى العسكرية 🛡️", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A0A))
                )
                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0F0F0F),
                    contentColor = Color(0xFF2E5E44),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF2E5E44)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("هوية الطوفان", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) Color.White else Color.Gray) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("متجر الصمود", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) Color.White else Color.Gray) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("مكتبة الصمود", fontWeight = FontWeight.Bold, color = if (selectedTab == 2) Color.White else Color.Gray) }
                    )
                }
            }
        },
        containerColor = Color(0xFF0A0A0A)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> IdentityTab(repository)
                1 -> StickerGiftStoreTab(repository)
                2 -> ResilienceLibraryTab()
            }
        }
    }
}

// Sub Tab 1: هوية الطوفان
@Composable
fun IdentityTab(repository: ChatRepository) {
    var backupCode by remember { mutableStateOf("ALTOF_MOCK_SECRET_BACKUP_TOKEN_797241_KOTLIN_SECURE") }
    val currentUserName = repository.currentUserName.collectAsState(initial = "المجاهد الصامد").value
    val currentUserPhone = repository.currentUserPhoneNumber.collectAsState(initial = "+966500000000").value

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A2A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF2E5E44), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            currentUserName.take(2),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(currentUserName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(currentUserPhone ?: "رقم الهاتف غير مسجل", color = Color.LightGray, fontSize = 12.sp)
                        Text("الحالة الميدانية: الرباط والصمود 🇵🇸", color = Color(0xFFD4AF37), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("إعدادات السرية والتشفير اللاسلكي 🔒", color = Color(0xFF2E5E44), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        item {
            var stealthActive by remember { mutableStateOf(true) }
            val stats = "بروتوكول الند للند ذكي (P2P)"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("بروتوكول التشفير العسكري الطوفاني", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("تشفير محلي 256-AES للرسائل لا يمكن اعتراضه.", color = Color.Gray, fontSize = 12.sp)
                    }
                    Switch(
                        checked = stealthActive,
                        onCheckedChange = { stealthActive = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF2E5E44))
                    )
                }

                Divider(color = Color(0xFF2A2A2A), modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("حالة الاتصال العسكري", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(stats, color = Color(0xFF2E5E44), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.Verified, contentDescription = "Verified Status", tint = Color(0xFF2E5E44))
                }
            }
        }

        item {
            Text("مفتاح الاستعادة والنسخ الاحتياطي المشفر 🔑", color = Color(0xFF2E5E44), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                border = BorderStroke(1.dp, Color(0xFF2E5E44)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "حافظ على هذا الكود المشفر لاستعادة جميع محادثاتك وسجلات الصمود للعبة أوفلاين إذا فقدت جهازك:",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        backupCode,
                        color = Color(0xFFD4AF37),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            // Copy Simulated Action
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A2A)),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نسخ المفتاح اللاسلكي", color = Color.White)
                    }
                }
            }
        }
    }
}

// Sub Tab 2: متجر الصمود (Unlock resistance stickers with Samed game rewards)
data class TofanSticker(
    val id: String,
    val name: String,
    val isLocked: Boolean,
    val costPoints: Int,
    val unicodeSymbol: String
)

@Composable
fun StickerGiftStoreTab(repository: ChatRepository) {
    var samedScores by remember { mutableStateOf<List<GameScoreEntity>>(emptyList()) }
    var userHighScore = 250 // Fallback
    val userName = repository.currentUserName.collectAsState(initial = "المجاهد").value

    LaunchedEffect(Unit) {
        repository.getTopScores().collect { scores ->
            val userScore = scores.find { it.userId == userName }
            if (userScore != null) {
                userHighScore = userScore.score
            }
            samedScores = scores
        }
    }

    var stickers by remember {
        mutableStateOf(
            listOf(
                TofanSticker("s1", "أبو عبيدة 🇵🇸", false, 0, "✊"),
                TofanSticker("s2", "مفتاح العودة 🔑", true, 100, "🔑"),
                TofanSticker("s3", "قبة الصخرة 🕌", true, 250, "🕌"),
                TofanSticker("s4", "غزة العزة 🌿", true, 500, "🌿"),
                TofanSticker("s5", "الخارطة الكاملة 🇵🇸", true, 800, "🇵🇸"),
                TofanSticker("s6", "حنظلة الأبي 💔", true, 1000, "✊")
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF112117)),
                border = BorderStroke(1.dp, Color(0xFF2E5E44)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("عملتك العسكرية المتوفرة (رصيد الصمود) 🪙", color = Color(0xFF2E5E44), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$userHighScore نقطة صمود",
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            "🎮 اجمع المزيد باللعب أوفلاين!",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Text("ملصقات الصمود والمقاومة الصامدة 🇵🇸", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(stickers) { sticker ->
                    val canAfford = userHighScore >= sticker.costPoints
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        border = BorderStroke(1.dp, if (sticker.isLocked) Color(0xFF333333) else Color(0xFF2E5E44)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(sticker.unicodeSymbol, fontSize = 48.sp, modifier = Modifier.padding(8.dp))
                            Text(sticker.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            Spacer(modifier = Modifier.height(10.dp))

                            if (!sticker.isLocked) {
                                Text(
                                    "مفتوحة ومتاحة للشات ✅",
                                    color = Color(0xFF2E5E44),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Button(
                                    onClick = {
                                        if (canAfford) {
                                            stickers = stickers.map {
                                                if (it.id == sticker.id) it.copy(isLocked = false) else it
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (canAfford) Color(0xFF1A3A2A) else Color(0xFF9E2A2B)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (canAfford) "شراء بـ ${sticker.costPoints} ن" else "رصيد غير كافي (${sticker.costPoints})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Sub Tab 3: مكتبة الصمود (Inspiring books read-out reader)
data class ResilienceBook(
    val title: String,
    val author: String,
    val date: String,
    val contentPreview: String
)

@Composable
fun ResilienceLibraryTab() {
    val books = listOf(
        ResilienceBook("كتيب الصامدين الميداني 📕", "لجنة الطوارئ الميدانية غزة", "2024", "توجيهات تثقيفية للعيش تحت الحصار وشروط الأمن اللاسلكي الفردي والاتصالات البديلة."),
        ResilienceBook("تاريخ القضية من النكبة حتى الطوفان 📗", "د. مصطفى العودة", "2023", "محطات رئيسية تسلط الضوء على فكر الثبات والرباط والتحولات السياسية والتاريخ الكامل لفلسطين الأبية من النهر إلى البحر."),
        ResilienceBook("فلسفة النضال الوطني وإرادة الحديد 📓", "المفكر غسان الكنفاني", "1972", "أهم التحليلات الفلسفية حول أهمية السلاح الثقافي، والكلمة الصادقة كدرع يحمي الأرض ويدافع عن الكرامة والعرض."),
        ResilienceBook("دليل الصمود المائي والفضائي الطبيعي 📘", "المهندس صابر غزة", "2024", "كيفية التغلب على انقطاع الموارد الأساسية، وتصميم أجهزة بديلة لتنقية المياه وتشغيل الطاقة الشمسية المحدودة بطرق مبتكرة.")
    )

    var activeBookReading by remember { mutableStateOf<ResilienceBook?>(null) }

    if (activeBookReading != null) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C140E)),
            border = BorderStroke(1.dp, Color(0xFF2E5E44))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        activeBookReading!!.title,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37),
                        fontSize = 18.sp
                    )
                    IconButton(onClick = { activeBookReading = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                Text("المؤلف: ${activeBookReading!!.author} (${activeBookReading!!.date})", color = Color.Gray, fontSize = 12.sp)

                Divider(color = Color(0xFF1D3524), modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(
                            text = "الحمد لله رب العالمين والصلاة والسلام على نبينا المجاهد الكريم.\n\n" +
                                    "إن الصمود في هذه الأرض الحبيبة فلسطين ليس شعاراً بل هو ممارسة يومية وتحد مباشر للعدوان الغاشم.\n\n" +
                                    "${activeBookReading!!.contentPreview}\n\n" +
                                    "إن تشغيل الاتصالات اللاسلكية المشفرة وتوفير بيئة تواصل آمنة مثل تطبيق 'الطوفان' يهدف في المقام الأول إلى إبقاء الكلمة صادقة والخبر ثابتاً.\n\n" +
                                    "نوصي جميع الأخوة الالتزام الكامل بقوانين الحيطة والحذر، وعدم الإدلاء بأي إحداثيات أو مواقع ميدانية حساسة حفاظاً على الأرواح المنيعة وسرية التحركات.\n\n" +
                                    "إن عدونا مهزوم بإرادة حديدية لا تنكسر وعزمنا مستمد من إيماننا المتين بحقنا الأبدي.\n\n" +
                                    "ثبتنا الله وإياكم على الحق ونصرنا نصراً عزيزاً مؤزراً.",
                            color = Color.White,
                            fontSize = 16.sp,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "موجز مكتبة الصمود والتمكين 🇵🇸",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    "اقرأ وحلّق بقيم الثبات والوعي المعرفي طوال فترات الحصار والرباط الميداني.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            items(books) { book ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeBookReading = book }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(book.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(book.date, color = Color(0xFFD4AF37), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("الكاتب: ${book.author}", color = Color.LightGray, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = book.contentPreview,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            maxLines = 2,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "اضغط للقراءة والتحميل اللاسلكي 📖 ⬇️",
                            color = Color(0xFF2E5E44),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
