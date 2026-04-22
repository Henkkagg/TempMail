package domain

import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

class DeleteOldEmails(private val databaseRepository: DatabaseRepository) {

    suspend operator fun invoke() {
        val timeThreshold = java.time.Instant.now().minus(1L, ChronoUnit.HOURS)
        databaseRepository.removeMessagesOlderThan(timeThreshold)
    }
}