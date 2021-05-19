package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class HelpCommand(private val prefix: String, private val gitHubUrl: String) {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

        if ((args[0].equals(prefix, true) && args.count() == 1) || (args[0].equals(prefix, true) && args.count() == 2 && args[1].equals("help", true))) {
            val infoEmbed = EmbedBuilder()
                .setTitle("Dvyne Bot - Help")
                .setColor(Color.decode("#FDC901"))
                .setDescription("Thanks for using Dvyne!")
                .addField(MessageEmbed.Field("Commands", "None currently.", false))
                .addField("OpenSource", "I'm open-source!\nContribute at: \n$gitHubUrl", false)
                .setFooter("Creator: Skyslycer")
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendTyping().queue()
            event.channel.sendMessage(infoEmbed).queue()
        }
    }
}