package org.censusmate.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UserTable : Table("auth.users") {
    val id = uuid("id").autoGenerate()
    val email = text("email").uniqueIndex()
    val firstName = text("first_name").default("")
    val lastName = text("last_name").default("")
    val role = text("role").default("agent") // "agent" | "administrator"
    val isBlocked = bool("is_blocked").default(false)
    val defaultUser = bool("default_user").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(id)
}