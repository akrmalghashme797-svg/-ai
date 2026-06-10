package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepository(private val context: Context) {

    val db: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "al_tofan_chat_db"
        ).fallbackToDestructiveMigration().build()
    }

    private val chatDao = db.chatDao()
    private val messageDao = db.messageDao()
    private val gameScoreDao = db.gameScoreDao()

    // Active User State
    private val _currentUserPhoneNumber = MutableStateFlow<String?>(null)
    val currentUserPhoneNumber: StateFlow<String?> = _currentUserPhoneNumber

    private val _currentUserId = MutableStateFlow<String>("user_id_123")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _currentUserName = MutableStateFlow<String>("المجاهد الصامد")
    val currentUserName: StateFlow<String> = _currentUserName

    init {
        // Populate initial mock data in a coroutine so the app has content on boot
        CoroutineScope(Dispatchers.IO).launch {
            populateInitialMockDataIfNeeded()
        }
    }

    fun setLoggedInUser(phoneNumber: String, name: String) {
        _currentUserPhoneNumber.value = phoneNumber
        _currentUserName.value = name
        _currentUserId.value = "user_" + phoneNumber.takeLast(4)
    }

    fun logout() {
        _currentUserPhoneNumber.value = null
    }

    fun getActiveChats(): Flow<List<ChatEntity>> = chatDao.getActiveChats()
    fun getArchivedChats(): Flow<List<ChatEntity>> = chatDao.getArchivedChats()
    fun getMessages(chatId: String): Flow<List<MessageEntity>> = messageDao.getMessagesForChat(chatId)

    suspend fun insertMessage(chatId: String, content: String, type: String = "TEXT", attachmentSize: Long = 0, replyToId: String? = null, compress2G: Boolean = false) {
        val finalContent = if (compress2G && type == "TEXT") compressText(content) else content
        val currentPhone = currentUserPhoneNumber.value ?: "966500000000"
        val senderName = _currentUserName.value

        val message = MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = _currentUserId.value,
            senderName = senderName,
            content = finalContent,
            timestamp = System.currentTimeMillis(),
            status = "SENT",
            type = type,
            attachmentSize = attachmentSize,
            isDisappearing = false,
            isEdited = false,
            replyToId = replyToId,
            reactionEmojis = if (compress2G) "⚡" else ""
        )
        messageDao.insertMessage(message)

        // Update the last message on the chat entity
        val activeChat = getActiveChats().first().find { it.id == chatId }
            ?: getArchivedChats().first().find { it.id == chatId }

        if (activeChat != null) {
            val updatedChat = activeChat.copy(
                lastMessage = if (compress2G) "⚡ [مضغوط] $finalContent" else finalContent,
                lastTimestamp = System.currentTimeMillis()
            )
            chatDao.updateChat(updatedChat)
        }

        // Simulate an auto-reply from contact if it's not a channel or group
        if (activeChat != null && activeChat.type == "INDIVIDUAL") {
            simulateAutoReply(chatId, activeChat.name)
        }
    }

    private suspend fun simulateAutoReply(chatId: String, senderName: String) {
        kotlinx.coroutines.delay(2000) // Delay before reply

        val replies = listOf(
            "معكم على العهد والميثاق دائماً. 🇵🇸",
            "طوفاننا مستمر حتى نيل حريتنا بالكامل والعودة لأراضينا.",
            "بارك الله فيكم يا غالي. هل راجعت مستندات مكتبة الصمود اليوم؟",
            "تم الاستلام، تواصل آمن ومشفر 100% عبر خوادمنا العسكرية.",
            "فداكم الروح والولد، النصر لنا بالتأكيد والعدوان إلى زوال."
        )
        val replyContent = replies.random()

        val reply = MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = "sender_$chatId",
            senderName = senderName,
            content = replyContent,
            timestamp = System.currentTimeMillis(),
            status = "READ",
            type = "TEXT",
            attachmentSize = 0,
            isDisappearing = false,
            isEdited = false,
            replyToId = null,
            reactionEmojis = ""
        )
        messageDao.insertMessage(reply)

        // Update last message in chat
        val activeChat = getActiveChats().first().find { it.id == chatId }
            ?: getArchivedChats().first().find { it.id == chatId }

        if (activeChat != null) {
            val updatedChat = activeChat.copy(
                lastMessage = replyContent,
                lastTimestamp = System.currentTimeMillis(),
                unreadCount = activeChat.unreadCount + 1
            )
            chatDao.updateChat(updatedChat)
        }
    }

    // Toggle Pin status of chat
    suspend fun togglePin(chatId: String) {
        val activeChats = getActiveChats().first()
        val chat = activeChats.find { it.id == chatId } ?: getArchivedChats().first().find { it.id == chatId }
        if (chat != null) {
            val updated = chat.copy(isPinned = !chat.isPinned)
            chatDao.updateChat(updated)
        }
    }

    // Archive / Unarchive Chat
    suspend fun toggleArchive(chatId: String) {
        val allChats = getActiveChats().first() + getArchivedChats().first()
        val chat = allChats.find { it.id == chatId }
        if (chat != null) {
            val updated = chat.copy(isArchived = !chat.isArchived)
            chatDao.updateChat(updated)
        }
    }

    // Add emoji reaction
    suspend fun addReaction(messageId: String, emoji: String) {
        // Simplistic search for message and updates reaction
        // For local purposes we'll run a select query or simulation.
        // We can load specific message, update locally.
    }

    // 2G compression routine
    fun compressText(text: String): String {
        var comp = text
        // Simple map of common Arabic tactical words to abbreviations to fit tiny 2GB-like packaging
        val abbreviations = mapOf(
            "السلام عليكم ورحمة الله وبركاته" to "س.ع",
            "السلام عليكم" to "س.ع",
            "وعليكم السلام" to "وع.س",
            "بسم الله الرحمن الرحيم" to "بسم الله",
            "الله يحفظكم" to "الله يحميك",
            "قطاع غزة" to "القطاع",
            "المقاومة الفلسطينية" to "المقاو_مة",
            "المستجدات الميدانية" to "الوضع",
            "في سبيل الله" to "ف.س.ل",
            "إن شاء الله" to "إن_ش_ء"
        )
        abbreviations.forEach { (keyword, abbrev) ->
            comp = comp.replace(keyword, abbrev)
        }
        return "⚡$comp"
    }

    fun decompressText(text: String): String {
        if (!text.startsWith("⚡")) return text
        var decomp = text.removePrefix("⚡")
        val map = mapOf(
            "س.ع" to "السلام عليكم ورحمة الله وبركاته",
            "وع.س" to "وعليكم السلام ورحمة الله وبركاته",
            "المقاو_مة" to "المقاومة الفلسطينية",
            "القطاع" to "قطاع غزة",
            "الوضع" to "المستجدات الميدانية",
            "ف.س.ل" to "في سبيل الله",
            "إن_ش_ء" to "إن شاء الله"
        )
        map.forEach { (abbrev, original) ->
            decomp = decomp.replace(abbrev, original)
        }
        return decomp
    }

    // Game scores management
    fun getTopScores(): Flow<List<GameScoreEntity>> = gameScoreDao.getTopScores()

    suspend fun saveGameScore(score: Int) {
        val badge = when {
            score >= 1000 -> "صامد ذهبي 🎖️"
            score >= 500 -> "صامد فضي 🥈"
            else -> "صامد برونزي 🥉"
        }
        val entity = GameScoreEntity(
            userId = _currentUserName.value,
            score = score,
            timestamp = System.currentTimeMillis(),
            localBadgeType = badge,
            isSynced = true
        )
        gameScoreDao.saveScore(entity)
    }

    // Database bootstrapping
    private suspend fun populateInitialMockDataIfNeeded() {
        val active = chatDao.getActiveChats().first()
        val archived = chatDao.getArchivedChats().first()
        if (active.isEmpty() && archived.isEmpty()) {
            // Seed chats
            val initialChats = listOf(
                ChatEntity("chat_1", "أبو عبيدة 🇵🇸", "INDIVIDUAL", "كتائب القسام ستلقي كلمة هامة خلال دقائق.", System.currentTimeMillis() - 300000, 2, isPinned = true, statusText = "متصل حالياً"),
                ChatEntity("chat_2", "قناة طوفان الأقصى الرسمية 📢", "CHANNEL", "عاجل | استهداف دبابة ميركافا في محور شمال غزة بعبوة العمل الفدائي 💥", System.currentTimeMillis() - 600000, 5, isPinned = true, statusText = "124,500 مشترك"),
                ChatEntity("chat_3", "مجموعتنا العائلية 👨‍👩‍👧‍👦", "GROUP", "محمد: الحمد لله نحن صامدون وبخير ولا تقلقوا علينا.", System.currentTimeMillis() - 1200000, 0, isPinned = false, statusText = "18 عضو، 3 متصلين"),
                ChatEntity("chat_4", "النشامى الأحرار 🇯🇴🇵🇸", "GROUP", "عمر: المساعدات الأردنية البرية تدخل غزة الآن عبر رفح.", System.currentTimeMillis() - 3600000, 0, isPinned = false, statusText = "120 عضو"),
                ChatEntity("chat_5", "مكتبة الصمود الرقمية 📚", "CHANNEL", "تم رفع كتاب: فلسفة النضال الوطني وإرادة الحديد.", System.currentTimeMillis() - 7200000, 0, isPinned = false, isArchived = true, statusText = "أرشيف الصمود")
            )

            initialChats.forEach { chatDao.insertChat(it) }

            // Seed initial messages for Chat 1 (Abu Obeida)
            val msgs1 = listOf(
                MessageEntity("m1_1", "chat_1", "sender_chat_1", "أبو عبيدة 🇵🇸", "السلام عليكم ورحمة الله وبركاته يا حماة الوطن.", System.currentTimeMillis() - 1800000, "READ", "TEXT"),
                MessageEntity("m1_2", "chat_1", "user_id_123", "المجاهد الصامد", "وعليكم السلام يا قائدنا المفدى. نحن ثابتون ههنا.", System.currentTimeMillis() - 1200000, "READ", "TEXT"),
                MessageEntity("m1_3", "chat_1", "sender_chat_1", "أبو عبيدة 🇵🇸", "كتائب القسام ستبث بياناً عسكرياً هاماً بعد قليل، ترقبوه وانشروه لرفع المعنويات.", System.currentTimeMillis() - 300000, "DELIVERED", "TEXT")
            )
            msgs1.forEach { messageDao.insertMessage(it) }

            // Seed initial messages for Chat 2 (Official Channel)
            val msgs2 = listOf(
                MessageEntity("m2_1", "chat_2", "sender_chat_2", "قناة طوفان الأقصى الرسمية 📢", "بيان صادر عن المتحدث باسم القوات المسلحة حول تدمير آليات للعدو.", System.currentTimeMillis() - 3600000, "READ", "TEXT"),
                MessageEntity("m2_2", "chat_2", "sender_chat_2", "قناة طوفان الأقصى الرسمية 📢", "عاجل | استهداف دبابة ميركافا في محور شمال غزة بعبوة العمل الفدائي 💥", System.currentTimeMillis() - 600000, "DELIVERED", "IMAGE", attachmentSize = 1048576)
            )
            msgs2.forEach { messageDao.insertMessage(it) }

            // Seed initial Game Scores
            val initialScores = listOf(
                GameScoreEntity("المجاهد الصامد", 1250, System.currentTimeMillis() - 10000, "صامد ذهبي 🎖️"),
                GameScoreEntity("أحمد غزة", 950, System.currentTimeMillis() - 20000, "صامد فضي 🥈"),
                GameScoreEntity("نشامى المقاومة", 620, System.currentTimeMillis() - 30000, "صامد فضي 🥈"),
                GameScoreEntity("سناء الأقصى", 340, System.currentTimeMillis() - 40000, "صامد برونزي 🥉")
            )
            initialScores.forEach { gameScoreDao.saveScore(it) }
        }
    }
}
