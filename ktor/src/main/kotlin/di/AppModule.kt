package di

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import data.DatabaseRepositoryImpl
import domain.DatabaseRepository
import domain.DeleteOldEmails
import domain.GenerateEmailAddress
import domain.SubscriptionService
import model.WordLists
import org.koin.dsl.module
import org.postgresql.PGConnection
import javax.sql.DataSource

val appModule = module {
    single<WordLists> {
        WordLists(
            object {}.javaClass.getResourceAsStream("/wordlist1.txt")!!.bufferedReader().readLines(),
            object {}.javaClass.getResourceAsStream("/wordlist2.txt")!!.bufferedReader().readLines()
        )
    }
    single<DataSource> { createDbConnection() }
    //For notifications
    single<PGConnection> {
        val connection = get<DataSource>().connection
        connection.createStatement().execute("LISTEN notification_channel")
        connection.unwrap(PGConnection::class.java)
    }
    single<DatabaseRepository> { DatabaseRepositoryImpl(get()) }
    single { SubscriptionService(get()) }
    single { GenerateEmailAddress(get(), get()) }
    single { DeleteOldEmails(get()) }
}

private fun createDbConnection(): DataSource {
    val config = HikariConfig()

    val dbName = "postgres"
    val host = "postgres"
    config.jdbcUrl = "jdbc:postgresql://${host}:5432/${dbName}"
    config.username = "postgres"
    config.password = "postgres"

    return HikariDataSource(config)
}