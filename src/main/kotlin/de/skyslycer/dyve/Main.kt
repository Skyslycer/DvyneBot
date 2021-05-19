package de.skyslycer.dyve

import de.skyslycer.dyve.commands.Command
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import java.io.File
import java.util.*

fun main() {
    val token: ByteArray = Base64.getDecoder().decode(File("src/main/resources/token.txt").readText())
    val jda = JDABuilder.createDefault(String(token))

    var prefix = "d!"
    val gitHubUrl = "https://github.com/Skyslycer/DyveBot"

    jda.setActivity(Activity.playing("mit deinem Leben"))
    jda.addEventListeners(Command(prefix, gitHubUrl))
    jda.build()
}