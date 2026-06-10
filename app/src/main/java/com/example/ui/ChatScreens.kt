package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatEntity
import com.example.data.ChatRepository
import com.example.data.MessageEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ---------------- SINE WAVE DRAWER (FOR AGORA & MICROPHONE REC) ----------------
@Composable
fun RollingSoundWave(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFD4AF37),
    amplitude: Float = 1.0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val points = 80
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, h / 2f)
            for (i in 0..points) {
                val x = (i.toFloat() / points) * w
                val sine = kotlin.math.sin((i.toFloat() / points) * 4f * Math.PI.toFloat() - phase)
                val envelope = kotlin.math.sin((i.toFloat() / points) * Math.PI.toFloat()) // smooth pinch edges
                val y = (h / 2f) + sine.toFloat() * envelope.toFloat() * (h * 0.45f) * amplitude
                lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
        )
    }
}


// ---------------- CHAT LIST SCREEN (MAIN HUB) ----------------
@Composable
fun ChatListScreen(
    repository: ChatRepository,
    onChatClick: (String) -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateGame: () -> Unit
) {
    var searchKeyword by remember { mutableStateOf("") }
    var networkStatus by remember { mutableStateOf(0) } // 0 = Online, 1 = Low 2G Network, 2 = Offline 🎮
    var selectedGroupTab by remember { mutableStateOf(0) } // 0 = الكل, 1 = القنوات, 2 = المجموعات, 3 = المؤرشفة

    val activeChats by repository.getActiveChats().collectAsState(initial = emptyList())
    val archivedChats by repository.getArchivedChats().collectAsState(initial = emptyList())

    val allChatsList = if (selectedGroupTab == 3) archivedChats else activeChats

    // Filter list based on search and tab selections
    val filteredChats = allChatsList.filter { chat ->
        val matchSearch = chat.name.contains(searchKeyword, ignoreCase = true) ||
                chat.lastMessage.contains(searchKeyword, ignoreCase = true)
        val matchTab = when (selectedGroupTab) {
            0 -> true // All
            1 -> chat.type == "CHANNEL"
            2 -> chat.type == "GROUP"
            3 -> true // Archived is pre-isolated
            else -> true
        }
        matchSearch && matchTab
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF0A0A0A))) {
                // Main Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AlTofanLogo(modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "طـوفـان الـتـواصـل 🇵🇸",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            // Network Badges Selector (Online / 2G / Offline)
                            Row(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .clickable {
                                        networkStatus = (networkStatus + 1) % 3
                                    }
                                    .background(
                                        when (networkStatus) {
                                            0 -> Color(0xFF132A1C)
                                            1 -> Color(0xFF33250A)
                                            else -> Color(0xFF3A1112)
                                        },
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            when (networkStatus) {
                                                0 -> Color.Green
                                                1 -> Color.Yellow
                                                else -> Color.Red
                                            },
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (networkStatus) {
                                        0 -> "آمن ومتصل (تشفير كمي)"
                                        1 -> "ضعيف (تحسين 2G نشط ⚡)"
                                        else -> "أوفلاين (شغل اللعبة 🎮)"
                                    },
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Settings link icon
                    IconButton(onClick = onNavigateSettings, modifier = Modifier.testTag("settings_button")) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }

                // Interactive search bar filter
                OutlinedTextField(
                    value = searchKeyword,
                    onValueChange = { searchKeyword = it },
                    placeholder = { Text("ابحث عن المحادثات أو القنوات...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("search_chats_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF141414),
                        unfocusedContainerColor = Color(0xFF141414),
                        focusedBorderColor = Color(0xFF2E5E44),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                    singleLine = true
                )

                // Category Switch Tabs (All, Channels, Groups, Archived)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf("كل المحادثات", "القنوات الرسمية", "المجموعات", "الأرشيف العسكري 📁")
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedGroupTab == index) Color(0xFF1A3A2A) else Color(0xFF141414))
                                .clickable { selectedGroupTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedGroupTab == index) Color.White else Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF0A0A0A),
        // Floating action button 🎮 is showing when network is simulated offline (status == 2) or available as fallback!
        floatingActionButton = {
            AnimatedVisibility(
                visible = networkStatus == 2,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onNavigateGame,
                    containerColor = Color(0xFFD4AF37),
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("floating_game_button")
                ) {
                    Icon(
                        Icons.Default.Games,
                        contentDescription = "Play offline Game Samed",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (filteredChats.isEmpty()) {
                // Empty State feedback
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Message, contentDescription = "No chats", tint = Color.DarkGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("لا توجد محادثات تطابق البحث!", color = Color.Gray, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("انضم للقنوات أو غير تبويب الفلتر أعلاه.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredChats) { chat ->
                        ChatListItemRow(
                            chat = chat,
                            onClick = { onChatClick(chat.id) },
                            onLongPress = {
                                // Simulate localized pin/archive quick action
                            }
                        )
                        Divider(color = Color(0xFF141414))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItemRow(
    chat: ChatEntity,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sdf = SimpleDateFormat("HH:mm a", Locale.getDefault())
    val formattedTime = sdf.format(Date(chat.lastTimestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongPress() }
                )
            }
            .background(if (chat.isPinned) Color(0xFF0F1B14) else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mock Avatar (initials with custom olive gradient color background depending on type)
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    if (chat.type == "CHANNEL") Color(0xFF1E3524) else Color(0xFF2E2E2E),
                    CircleShape
                )
                .border(
                    BorderStroke(1.5.dp, if (chat.type == "CHANNEL") Color(0xFFD4AF37) else Color.Transparent),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val symbol = when (chat.type) {
                "CHANNEL" -> "📢"
                "GROUP" -> "👥"
                else -> chat.name.take(1)
            }
            Text(
                text = symbol,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Chat Details Column
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formattedTime,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.lastMessage,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (chat.isPinned) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = Color(0xFF2E5E44),
                            modifier = Modifier
                                .size(14.dp)
                                .padding(end = 4.dp)
                        )
                    }

                    if (chat.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2E5E44), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


// ---------------- CHAT SCREEN WITH MESSAGES & SIMULATORS ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    repository: ChatRepository,
    chatId: String,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    var chatSelected by remember { mutableStateOf<ChatEntity?>(null) }
    var rawTextMsg by remember { mutableStateOf("") }
    var raw2GEmergencyMode by remember { mutableStateOf(false) }
    var activeVoipCallState by remember { mutableStateOf(false) } // Simulated VoIP popup
    var showStickersDrawer by remember { mutableStateOf(false) } // Simulated custom sticker drawer
    var messageToReplyTo by remember { mutableStateOf<MessageEntity?>(null) } // Reply-to helper

    // Mic Hold Recording Emulator States
    var isRecordingVoicenote by remember { mutableStateOf(false) }
    var voicenoteAmplitude by remember { mutableStateOf(0.1f) }
    var recordingTimerSeconds by remember { mutableStateOf(0) }

    val messagesList by repository.getMessages(chatId).collectAsState(initial = emptyList())

    // Load current chat entity details by collecting target stream securely inside coroutines
    LaunchedEffect(chatId) {
        launch {
            repository.getActiveChats().collect { list ->
                val chat = list.find { it.id == chatId }
                if (chat != null) {
                    chatSelected = chat
                }
            }
        }
        launch {
            repository.getArchivedChats().collect { list ->
                val chat = list.find { it.id == chatId }
                if (chat != null) {
                    chatSelected = chat
                }
            }
        }
    }

    // Scroll to bottom when message size increments
    LaunchedEffect(messagesList.size) {
        if (messagesList.isNotEmpty()) {
            listState.animateScrollToItem(messagesList.size - 1)
        }
    }

    // Recording Voice note Timer Loop
    LaunchedEffect(isRecordingVoicenote) {
        if (isRecordingVoicenote) {
            recordingTimerSeconds = 0
            while (isRecordingVoicenote) {
                delay(100)
                voicenoteAmplitude = (kotlin.math.sin(System.currentTimeMillis().toDouble() / 150.0).toFloat() * 0.4f + 0.6f) + (Math.random().toFloat() * 0.2f)
                delay(900)
                recordingTimerSeconds++
            }
        }
    }

    Scaffold(
        topBar = {
            if (chatSelected != null) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF2E2E2E), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(chatSelected!!.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(chatSelected!!.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(chatSelected!!.statusText, color = Color.LightGray, fontSize = 11.sp)
                            }
                        }
                    },
                    actions = {
                        // Agora Audio VoIP Call Action
                        IconButton(onClick = { activeVoipCallState = true }, modifier = Modifier.testTag("voip_call_button")) {
                            Icon(Icons.Default.Phone, contentDescription = "Agora VoIP Audio call", tint = Color(0xFF2E5E44))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0F0F))
                )
            }
        },
        containerColor = Color(0xFF0A0A0A)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main messages feed area
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    item {
                        // Notice about encryption and servers!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .background(Color(0x221A3A2A), RoundedCornerShape(10.dp))
                                .border(BorderStroke(0.5.dp, Color(0xFF1D3524)), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "🔐 تم تشفير هذه المحادثة بالكامل بنظام الند للند عسكري مقاوم للاختراق.\nلا تشارك أي إحداثيات أو مواقع جغرافية حية حفاظاً على سلامتك.",
                                color = Color.LightGray,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    items(messagesList) { msg ->
                        MessageBubbleRow(
                            message = msg,
                            repository = repository,
                            onReplySwipe = {
                                messageToReplyTo = msg
                            }
                        )
                    }
                }

                // Interactive footer inputs
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F0F0F))
                        .padding(8.dp)
                ) {
                    // Reply to preview banner
                    if (messageToReplyTo != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "الرد على: ${messageToReplyTo!!.senderName}",
                                    color = Color(0xFF2E5E44),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = messageToReplyTo!!.content,
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(onClick = { messageToReplyTo = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // 2G Low Bandwidth emergency mode toggle indicator bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = raw2GEmergencyMode,
                                onCheckedChange = { raw2GEmergencyMode = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF2E5E44)),
                                modifier = Modifier.scale(0.81f).testTag("emergency_2g_switch")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "وضع الطوارئ (2G Optimized النصي المضغوط لضعف الإنترنت) ⚡",
                                color = if (raw2GEmergencyMode) Color(0xFFD4AF37) else Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bottom Keyboard Inputs Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sticker / GIF Drawer Toggle
                        IconButton(onClick = { showStickersDrawer = !showStickersDrawer }, modifier = Modifier.testTag("sticker_drawer_button")) {
                            Icon(
                                if (showStickersDrawer) Icons.Default.Keyboard else Icons.Default.InsertEmoticon,
                                contentDescription = "Stickers & GIFs",
                                tint = Color.Gray
                            )
                        }

                        // Text input field filled Material 3 style
                        OutlinedTextField(
                            value = rawTextMsg,
                            onValueChange = { rawTextMsg = it },
                            placeholder = { Text("اكتب رسالة مشفرة...", color = Color.Gray, fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("message_input_field"),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF1A1A1A),
                                unfocusedContainerColor = Color(0xFF141414),
                                focusedBorderColor = Color(0xFF2E5E44),
                                unfocusedBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Mic recorder OR Send depending on string buffer state!
                        val isTextEmpty = rawTextMsg.trim().isEmpty()

                        if (isTextEmpty) {
                            // Voice recorder button with HOLD gesture simulation
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isRecordingVoicenote) Color(0xFF9E2A2B) else Color(0xFF1E1E1E))
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                isRecordingVoicenote = true
                                                tryAwaitRelease()
                                                // On release logic
                                                isRecordingVoicenote = false
                                                coroutineScope.launch {
                                                    repository.insertMessage(
                                                        chatId = chatId,
                                                        content = "🎤 رسالة صوتية مشفرة (${recordingTimerSeconds} ثواني)",
                                                        type = "VOICE",
                                                        attachmentSize = 12048
                                                    )
                                                }
                                            }
                                        )
                                    }
                                    .testTag("hold_to_record_mic"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = "Hold to record Voice note",
                                    tint = Color.White
                                )
                            }
                        } else {
                            // Send text message action
                            IconButton(
                                onClick = {
                                    val tempText = rawTextMsg
                                    rawTextMsg = ""
                                    val replyId = messageToReplyTo?.id
                                    messageToReplyTo = null
                                    coroutineScope.launch {
                                        repository.insertMessage(
                                            chatId = chatId,
                                            content = tempText,
                                            type = "TEXT",
                                            replyToId = replyId,
                                            compress2G = raw2GEmergencyMode
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color(0xFF1A3A2A), CircleShape)
                                    .testTag("send_message_button")
                            ) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Send Message",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Holding Note Voice Record Interactive Overlay
                    AnimatedVisibility(
                        visible = isRecordingVoicenote,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .background(Color(0xFF2B0A0C), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color.Red, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "جاري تسجيل صوتي آمن (أفلت للإرسال): 0:${recordingTimerSeconds.toString().padStart(2, '0')}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Active animated sound amplitude vector wave representation
                            RollingSoundWave(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(24.dp),
                                color = Color.Red,
                                amplitude = voicenoteAmplitude
                            )
                        }
                    }

                    // Stickers Floating grid selection board
                    AnimatedVisibility(
                        visible = showStickersDrawer,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text("ملصقات طوفان الأحرار 🇵🇸", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                val resistanceEmojis = listOf("✊", "🔑", "🕌", "🌿", "🇵🇸")
                                resistanceEmojis.forEach { emoji ->
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .background(Color(0xFF1E1E1E), RoundedCornerShape(10.dp))
                                            .clickable {
                                                showStickersDrawer = false
                                                coroutineScope.launch {
                                                    repository.insertMessage(
                                                        chatId = chatId,
                                                        content = emoji, // Sending beautiful sticker as msg
                                                        type = "IMAGE",
                                                        attachmentSize = 10000000 // mock compress
                                                    )
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emoji, fontSize = 28.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ---------------- AGORA VOIP VOICES SIMULATOR TOP LAYOUT ----------------
            AnimatedVisibility(
                visible = activeVoipCallState,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xE60A0A0A))
                        .clickable { /* prevent clicks going behind */ }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color(0xFF1A3A2A), CircleShape)
                                .border(BorderStroke(2.dp, Color(0xFFD4AF37)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (chatSelected != null) chatSelected!!.name.take(2) else "🤝",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = chatSelected?.name ?: "مكالمة آمنة",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "جاري تفعيل محرك الاتصال التكتيكي Agora RTC...",
                            color = Color(0xFF2E5E44),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // High fidelity moving Agora Audio Sound wave display!
                        RollingSoundWave(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            color = Color(0xFF2E5E44),
                            amplitude = 1.3f
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mic Mute Button
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFF1E1E1E), CircleShape)
                            ) {
                                Icon(Icons.Default.MicOff, contentDescription = "Mute", tint = Color.White)
                            }

                            // Disconnect safe VoIP button
                            IconButton(
                                onClick = { activeVoipCallState = false },
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color(0xFF9E2A2B), CircleShape)
                                    .testTag("voip_disconnect_button")
                            ) {
                                Icon(Icons.Default.CallEnd, contentDescription = "Disconnect VoIP Agora Call", tint = Color.White, modifier = Modifier.size(32.dp))
                            }

                            // Volume High boost
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color(0xFF1E1E1E), CircleShape)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Speaker", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- MESSAGE BUBBLE COMPONENT ----------------
@Composable
fun MessageBubbleRow(
    message: MessageEntity,
    repository: ChatRepository,
    onReplySwipe: () -> Unit
) {
    val currentUserId = repository.currentUserId.collectAsState(initial = "user_id_123").value
    val isMyMessage = message.senderId == currentUserId

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
        ) {
            // Sender alias indicator in channel / group chat context
            if (!isMyMessage) {
                Text(
                    text = message.senderName,
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
                )
            }

            // Message Body Cards bubbles: #1A3A2A for sender, #1E1E1E for receiver.
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isMyMessage) Color(0xFF1A3A2A) else Color(0xFF1E1E1E)
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMyMessage) 16.dp else 2.dp,
                    bottomEnd = if (isMyMessage) 2.dp else 16.dp
                ),
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { onReplySwipe() }
                        )
                    }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Check if it's a generated sticker emoji to draw with extra large font!
                    val isSticker = message.type == "IMAGE" && (message.content == "✊" || message.content == "🔑" || message.content == "🕌" || message.content == "🌿" || message.content == "🇵🇸")

                    if (isSticker) {
                        Text(
                            text = message.content,
                            fontSize = 80.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else if (message.type == "VOICE") {
                        // Custom styled Audio Voice player representation card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play voice note", tint = Color.White)
                            // Wave representation
                            Canvas(modifier = Modifier.width(120.dp).height(20.dp)) {
                                val w = size.width
                                val h = size.height
                                val pointsCount = 15
                                for (i in 0 until pointsCount) {
                                    val barH = (kotlin.math.sin(i.toDouble() * 0.70) * 0.4 + 0.6) * h
                                    drawRect(
                                        color = Color.White,
                                        topLeft = Offset((i * w) / pointsCount, (h - barH.toFloat()) / 2f),
                                        size = Size(6f, barH.toFloat())
                                    )
                                }
                            }
                            Text("0:05", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Standard Text message
                        var contentToDraw = message.content
                        var isCompressed = false

                        if (contentToDraw.startsWith("⚡")) {
                            isCompressed = true
                        }

                        // Localized dynamic decompression click interaction!
                        var displayExtractedText by remember { mutableStateOf(contentToDraw) }

                        Text(
                            text = displayExtractedText,
                            color = Color.White,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )

                        // 2G decompression helper link
                        if (isCompressed && displayExtractedText.startsWith("⚡")) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "⚡ وضع الطوارئ نشط (اضغط لفك الضغط)",
                                color = Color(0xFFD4AF37),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        displayExtractedText = repository.decompressText(contentToDraw)
                                    }
                                    .background(Color(0xFF0A0A0A), RoundedCornerShape(4.dp))
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Inner timing and tick read status indicator
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val simpleTime = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date(message.timestamp))
                        Text(
                            text = simpleTime,
                            color = Color.Gray,
                            fontSize = 9.sp
                        )
                        if (isMyMessage) {
                            Spacer(modifier = Modifier.width(4.dp))
                            // Palestinian double tick read صحين checkmark!
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = "Message Read status",
                                tint = if (message.status == "READ" || message.reactionEmojis == "⚡") Color(0xFF2E5E44) else Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
