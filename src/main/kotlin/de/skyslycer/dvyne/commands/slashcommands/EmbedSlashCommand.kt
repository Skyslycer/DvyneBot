package de.skyslycer.dvyne.commands.slashcommands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

class EmbedSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient permission! **Permission:** ADMINISTRATOR").setEphemeral(true).queue()
            return
        }

        try {
            val embed = EmbedBuilder()
                .setTitle(event.getOption("title")!!.asString)
                .setDescription(event.getOption("content")!!.asString)
                .setFooter(event.user.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .setColor(
                    when(event.getOption("color")!!.asString.lowercase(Locale.ROOT)) {
                        "red" -> Color.RED
                        "blue" -> Color.BLUE
                        "green" -> Color.GREEN
                        "yellow" -> Color.YELLOW
                        "white" -> Color.WHITE
                        "black" -> Color.BLACK
                        "orange" -> Color.ORANGE
                        "pink" -> Color.PINK
                        "gray" -> Color.GRAY
                        "cyan" -> Color.CYAN
                        "magenta" -> Color.MAGENTA
                        else -> Color.decode(event.getOption("color")!!.asString)
                    }
                )
                .build()

            event.reply("Your embed got successfully created.").setEphemeral(true).queue()
            event.channel.sendMessage(embed).queue()

        } catch (exception: NumberFormatException) {
            event.reply("Your color input **${event.getOption("color")!!.asString}** isn't a color!").setEphemeral(true).queue()

            return
        }
    }
}