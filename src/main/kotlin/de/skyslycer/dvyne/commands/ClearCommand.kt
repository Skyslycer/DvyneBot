package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class ClearCommand(private val prefix: String) {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

        if (args.count() == 3 && args[0].equals(prefix, true) && args[1].equals("clear", true)) {
            try {
                val messages: List<Message> = event.channel.history.retrievePast(args[2].toInt()).complete()
                event.channel.deleteMessages(messages).queue()

                val successEmbed = EmbedBuilder()
                    .setTitle("Success!")
                    .setColor(Color.GREEN)
                    .setDescription("You deleted the last ${messages.size} messages!")
                    .setFooter("Creator: Skyslycer")
                    .setTimestamp(Instant.from(ZonedDateTime.now()))
                    .build()

                event.channel.sendMessage(successEmbed)
            } catch (exception: NumberFormatException) {
                val errorEmbed = EmbedBuilder()
                    .setTitle("Error")
                    .setColor(Color.RED)
                    .setDescription("Your 3rd argument needs to be a number!")
                    .setFooter("Creator: Skyslycer")
                    .setTimestamp(Instant.from(ZonedDateTime.now()))
                    .build()

                event.channel.sendMessage(errorEmbed)
            }
        } else if (args.count() == 2 && args[1].equals("clear", true)) {
            val successEmbed = EmbedBuilder()
                .setTitle("Wrong usage!")
                .setColor(Color.ORANGE)
                .setDescription("Please use $prefix clear (count)!")
                .setFooter("Creator: Skyslycer")
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(successEmbed)
        }
    }
}