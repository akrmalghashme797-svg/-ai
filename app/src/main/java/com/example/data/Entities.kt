package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "INDIVIDUAL", "GROUP", "CHANNEL"
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val avatarUrl: String = "",
    val statusText: String = ""
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val status: String, // "SENT", "DELIVERED", "READ"
    val type: String, // "TEXT", "IMAGE", "FILE", "VOICE"
    val attachmentSize: Long = 0,
    val isDisappearing: Boolean = false,
    val isEdited: Boolean = false,
    val replyToId: String? = null,
    val reactionEmojis: String = "" // comma separated
)

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey val userId: String,
    val score: Int,
    val timestamp: Long,
    val localBadgeType: String,
    val isSynced: Boolean = false
)

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE isArchived = 0 ORDER BY isPinned DESC, lastTimestamp DESC")
    fun getActiveChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE isArchived = 1 ORDER BY lastTimestamp DESC")
    fun getArchivedChats(): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Update
    suspend fun updateChat(chat: ChatEntity)

    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun deleteChat(id: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: String)
}

@Dao
interface GameScoreDao {
    @Query("SELECT * FROM game_scores ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<GameScoreEntity>>

    @Query("SELECT * FROM game_scores WHERE userId = :userId LIMIT 1")
    suspend fun getScoreForUser(userId: String): GameScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveScore(score: GameScoreEntity)
}

@Database(entities = [ChatEntity::class, MessageEntity::class, GameScoreEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun gameScoreDao(): GameScoreDao
}
