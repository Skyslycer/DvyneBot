package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class NukeCommand {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val confirmEmbed = EmbedBuilder()
            .setTitle("Nuke")
            .setColor(Color.RED)
            .setDescription("Do you really want to recreate this channel?")
            .setFooter(event.author.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        if (!event.member!!.hasPermission(Permission.MANAGE_CHANNEL)) {
            val noPermissionEmbed = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("No permission")
                .setDescription("You don't have permission for this command!")
                .addField(MessageEmbed.Field("Permission", "Manage channel", false))
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(noPermissionEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }

            return
        }

        event.channel.sendMessage(confirmEmbed).queue { message ->
            message.addReaction("✅").queue()
            message.addReaction("❌").queue()
        }
    }
}