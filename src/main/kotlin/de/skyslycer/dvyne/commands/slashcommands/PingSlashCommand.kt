package de.skyslycer.dvyne.commands.slashcommands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.button.Button
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class PingSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        event.jda.restPing.queue { time ->
            val pingEmbedBuilder = EmbedBuilder()
                .setTitle("Ping")
                .setDescription("The ping of the bot is ${time}ms")
                .setFooter(event.user.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))

            when {
                time <= 30 -> pingEmbedBuilder.setColor(Color.GREEN)
                time <= 100 -> pingEmbedBuilder.setColor(Color.ORANGE)
                else -> pingEmbedBuilder.setColor(Color.RED)
            }

            event.reply(MessageBuilder(pingEmbedBuilder).build())
                .addActionRow(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))
                .queue()
        }
    }
}