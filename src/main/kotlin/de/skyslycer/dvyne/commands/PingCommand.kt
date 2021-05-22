package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime

class PingCommand {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        event.jda.restPing.queue { time ->
            val pingEmbedBuilder = EmbedBuilder()
                .setTitle("Ping")
                .setDescription("The ping of the bot is ${time}ms")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))

            when {
                time <= 30 -> pingEmbedBuilder.setColor(Color.GREEN)
                time <= 100 -> pingEmbedBuilder.setColor(Color.ORANGE)
                else -> pingEmbedBuilder.setColor(Color.RED)
            }

            event.channel.sendMessage(pingEmbedBuilder.build()).queue { message ->
                message.addReaction("🗑").queue()
            }
        }
    }
}