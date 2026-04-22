package data

import domain.DatabaseRepository
import model.Email
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.updateReturning
import org.postgresql.PGConnection
import org.postgresql.PGNotification
import java.time.Instant

class DatabaseRepositoryImpl(private val pgConnection: PGConnection): DatabaseRepository {

    private object LcgSeedTable : Table("lcg_seed") {
        val seed = long("value")
    }

    private object EmailTable : Table("mail") {
        val id = integer("id").autoIncrement()
        val recipient = text("recipient")
        val sender = text("sender")
        val subject = text("subject")
        val body = text("body")
        val timestamp = timestampWithTimeZone("received_at")

        override val primaryKey = PrimaryKey(id)
    }

    override suspend fun getNextLcgValue(modulo: Long): Long {

        return suspendTransaction {
            LcgSeedTable.updateReturning {
                it[seed] = (seed * 69421L + 12345L) % modulo
            }.single()[LcgSeedTable.seed]
        }
    }

    override suspend fun getEmailsToAddress(address: String): List<Email> {

        return suspendTransaction {
            EmailTable.selectAll()
                .where { EmailTable.recipient eq address }
                .map { row ->
                    Email(
                        row[EmailTable.id],
                        row[EmailTable.sender],
                        row[EmailTable.recipient],
                        row[EmailTable.subject],
                        row[EmailTable.body]
                    )
                }
        }
    }

    override suspend fun getNotifications(): Array<PGNotification> {

        return pgConnection.notifications
    }

    /*
    Postgres stores timestamps in UTC. For whatever reason, when Exposed reads a normal timestamp, it assumes that the
    timestamp is in system's timezone (for example UTC +3). This causes exposed to read the timestamp wrong. A hacky
    solution is to read the timestamp as timestampWithTimezone, so exposed doesn't do any conversions. The timestamp
    is then read in Java's OffsetDateTime -format, so Java's Instant is used instead of Kotlin.

    Also, exposed "less" comparision is broken, and this fixes it, with n+1 db operations
     */
    override suspend fun removeMessagesOlderThan(timeThreshold: Instant) {

        suspendTransaction {
            EmailTable.selectAll().map { row ->

                val timestamp = row[EmailTable.timestamp].toInstant()
                if (timestamp.isBefore(timeThreshold)) {
                    EmailTable.deleteWhere { EmailTable.id eq row[EmailTable.id] }
                }
            }
        }
    }
}