package de.skyslycer.dvyne.core

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

class DatabaseManager {
    object reactions : Table() {
        val guild: Column<Long> = long("guild").uniqueIndex()
        val channel: Column<Long> = long("channel").uniqueIndex()
        val message: Column<Long> = long("message").uniqueIndex()
        val emoji: Column<String> = varchar("emoji", 34)
    }
}