package de.skyslycer.dvyne.commands.slashcommands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.button.Button
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class ClearSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        if (!event.member!!.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply("Insufficient permission! **Permission:** MANAGE_CHANNEL").setEphemeral(true).queue()
            return
        }

        val messagesToClear = event.getOption("count")!!.asLong

        if (messagesToClear > 100 || messagesToClear < 1) {
            event.reply("Your input must be a number **between 1 and 100!**").setEphemeral(true).queue()

            return
        }

        val messages: List<Message> = event.channel.history.retrievePast(messagesToClear.toInt()).complete()

        if (messages.count() < 1) {
            event.reply("There must be **at least 1 message** to delete1").setEphemeral(true).queue()
        }

        event.channel.purgeMessages(messages)

        val successEmbed = EmbedBuilder()
            .setTitle("Success!")
            .setColor(Color.GREEN)
            .setDescription("You deleted the last ${messages.size} messages!")
            .setFooter(event.user.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.reply(MessageBuilder(successEmbed).build())
            .addActionRow(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))
            .queue()
    }
}