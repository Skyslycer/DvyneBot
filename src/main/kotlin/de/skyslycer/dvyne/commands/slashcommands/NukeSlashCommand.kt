package de.skyslycer.dvyne.commands.slashcommands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.ActionRow
import net.dv8tion.jda.api.interactions.button.Button
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class NukeSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        if (!event.member!!.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply("Insufficient permission! **Permission:** MANAGE_CHANNEL").setEphemeral(true).queue()
            return
        }

        val confirmEmbed = EmbedBuilder()
            .setTitle("Nuke")
            .setColor(Color.RED)
            .setDescription("Do you really want to recreate this channel?")
            .setFooter(event.user.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.reply(MessageBuilder(confirmEmbed).build())
            .addActionRow(Button.success("${event.user.id}:delete", "Nevermind!"),
                Button.danger("${event.user.id}:nuke", "Yes, delete!"))
            .queue()
    }
}