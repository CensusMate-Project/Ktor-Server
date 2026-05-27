package org.censusmate.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object HouseholdTable : Table("census.households") {
    val id = uuid("id").autoGenerate()
    val enumeratorId = uuid("enumerator_id").references(UserTable.id).nullable()
    val eventId = uuid("event_id").references(EventTable.id).nullable()
    val address = text("address")
    val totalResidents = integer("total_residents")     // Total number of residents in the household
    val dwellingType = text("dwelling_type").nullable() // Type of dwelling (e.g., apartment, house)
    val buildingYear = text("building_year").nullable() // Year of building construction
    val totalArea = integer("total_area").nullable()    // Total area of the dwelling
    val livingArea = integer("living_area").nullable()  // Living area of the dwelling
    val roomsCount = integer("rooms_count").nullable()  // Number of rooms in the dwelling
    val notes = text("notes").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(id)
}