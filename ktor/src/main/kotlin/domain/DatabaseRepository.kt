package domain

import model.Email
import org.postgresql.PGNotification

interface DatabaseRepository {

    suspend fun getNextLcgValue(modulo: Long): Long

    suspend fun getEmailsToAddress(address: String): List<Email>

    suspend fun getNotifications(): Array<PGNotification>

    suspend fun removeMessagesOlderThan(timeThreshold: java.time.Instant)
}