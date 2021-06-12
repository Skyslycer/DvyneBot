package de.skyslycer.dvyne.commands.slashcommands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.button.Button
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class DmSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient permission! **Permission:** ADMINISTRATOR").setEphemeral(true).queue()
            return
        }

        val dmEmbed = EmbedBuilder()
            .setTitle(event.guild!!.name)
            .setColor(Color.GREEN)
            .setDescription("You received a direct message from a Discord server!")
            .addField(MessageEmbed.Field("Message", event.getOption("message")!!.asString, false))
            .setFooter(event.user.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.getOption("member")!!.asUser.openPrivateChannel()
            .flatMap { it.sendMessage(dmEmbed); it.close() }
            .queue()

        val successEmbed = EmbedBuilder()
            .setTitle("Message sent")
            .setColor(Color.GREEN)
            .setDescription("The user ${event.getOption("member")!!.asMember!!.asMention} successfully received the message!")
            .addField(MessageEmbed.Field("Message", event.getOption("message")!!.asString, false))
            .setFooter(event.user.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.reply(MessageBuilder(successEmbed).build())
            .addActionRow(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))
            .queue()
    }
}