package org.censusmate.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object PersonTable : Table("census.persons") {
    val id = uuid("id").autoGenerate()
    val householdId = uuid("household_id").references(HouseholdTable.id)
    val gender = text("gender").nullable()
    val birthDate = date("birth_date")
    val citizenship = text("citizenship").nullable()
    val hasDualCitizenship = bool("has_dual_citizenship").nullable()
    val nationality = text("nationality").nullable()
    val nativeLanguage = text("native_language").nullable()
    val speaksRussian = bool("speaks_russian").nullable()
    val otherLanguages = array<String>("other_languages").nullable()   // Languages spoken by the person
    val educationLevel = text("education_level").nullable()
    val maritalStatus = text("marital_status").nullable()
    val childrenCount = integer("children_count").nullable()
    val relationToHousehold = text("relation_to_household").nullable()
    val placeOfBirth = text("place_of_birth").nullable()
    val currentResidence = text("current_residence").nullable()
    val incomeSources =
        array<String>("income_sources").nullable() // Array: ["salary", "pension", "business", "other"]
    val employmentStatus =
        text("employment_status").nullable()       // For example: "employed", "unemployed", "student", "retired", "other"
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(id)
}