package org.censusmate.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UserAuthTable : Table("auth.user_auth") {
    val userId = uuid("user_id").references(UserTable.id)
    val password_hash = text("password_hash")
    val lastLogin = datetime("last_login").defaultExpression(CurrentDateTime)
    val lastPasswordChange = datetime("last_password_change").defaultExpression(CurrentDateTime)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(userId)
}
