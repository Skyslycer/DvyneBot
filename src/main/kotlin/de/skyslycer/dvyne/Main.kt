package de.skyslycer.dvyne

import de.skyslycer.dvyne.commands.Command
import de.skyslycer.dvyne.core.Tables
import de.skyslycer.dvyne.listeners.AddReactionListener
import de.skyslycer.dvyne.listeners.RemoveReactionListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun main() {
    Database.connect("jdbc:sqlite:src/main/resources/dvyne.db", "org.sqlite.JDBC")

    val jda = JDABuilder.createDefault(File("src/main/resources/token.txt").readText())

    val prefix = "d!"
    val gitHubUrl = "https://github.com/Skyslycer/DvyneBot"

    jda.setActivity(Activity.listening("d! or @Dvyne"))
    jda.addEventListeners(Command(prefix, gitHubUrl))
    jda.addEventListeners(AddReactionListener())
    jda.addEventListeners(RemoveReactionListener())
    jda.build()

    transaction {
        SchemaUtils.createMissingTablesAndColumns(Tables.Reactions)
    }
}