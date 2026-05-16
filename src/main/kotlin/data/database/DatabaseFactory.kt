package org.censusmate.data.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import org.censusmate.data.database.tables.UserAuthTable
import org.censusmate.data.database.tables.UserTable
import org.censusmate.security.PasswordHasher
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.server.application.log
import org.censusmate.config.Config
import org.censusmate.data.database.tables.EventTable
import org.jetbrains.exposed.sql.Schema

object DatabaseFactory {
    fun init(app: Application, config: Config.DatabaseConfig) {
        val config = HikariConfig().apply {
            jdbcUrl = config.url
            driverClassName = "org.postgresql.Driver"
            username = config.username
            password = config.password
            maximumPoolSize = config.maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createSchema(
                Schema("auth"),
                Schema("census")
            )
            SchemaUtils.createMissingTablesAndColumns(
                UserTable,
                UserAuthTable,
                EventTable
            )
        }

        app.log.info("Database initialized and connected successfully")
    }

    suspend fun <T> dbTransactionQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

fun Application.createDefaultAdminIfNotExists() {
    transaction {
        val adminExists = UserTable
            .selectAll()
            .where { UserTable.role eq "administrator" }
            .any()

        if (!adminExists) {
            log.warn("No admin found — creating default admin: admin@census.ru / admin123")

            val adminId = UserTable.insert {
                it[email] = "admin@census.ru"
                it[firstName] = "Admin"
                it[lastName] = "Census"
                it[role] = "administrator"
                it[defaultUser] = true
            }[UserTable.id]

            UserAuthTable.insert {
                it[userId] = adminId
                it[passwordHash] = PasswordHasher.hash("admin123")
            }
        }
    }
}