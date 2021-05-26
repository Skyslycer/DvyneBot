package de.skyslycer.dvyne.core

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

class Tables {
    object Reactions : Table("reactions") {
        val guild: Column<Long> = long("guild")
        val channel: Column<Long> = long("channel")
        val message: Column<Long> = long("message")
        val emoji: Column<String> = varchar("emoji", 34)
        val role: Column<Long> = long("role")
        override val primaryKey = PrimaryKey(guild, channel, message, emoji, role)
    }
}