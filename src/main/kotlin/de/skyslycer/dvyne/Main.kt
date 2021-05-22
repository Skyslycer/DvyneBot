package de.skyslycer.dvyne

import de.skyslycer.dvyne.commands.Command
import de.skyslycer.dvyne.core.DatabaseManager
import de.skyslycer.dvyne.listeners.AddReactionListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.vendors.DatabaseDialect
import java.io.File
import java.util.*

fun main() {
    val database = Database.connect("jdbc:sqlite:${File("src/main/resources/dbpath.txt").readText()}", "org.sqlite.JDBC")

    val jda = JDABuilder.createDefault(File("src/main/resources/token.txt").readText())

    var prefix = "d!"
    val gitHubUrl = "https://github.com/Skyslycer/DvyneBot"

    jda.setActivity(Activity.listening("d! or @Dvyne"))
    jda.addEventListeners(Command(prefix, gitHubUrl))
    jda.addEventListeners(AddReactionListener())
    jda.build()
}