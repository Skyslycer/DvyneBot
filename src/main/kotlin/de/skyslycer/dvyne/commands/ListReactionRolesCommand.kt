package de.skyslycer.dvyne.commands

import de.skyslycer.dvyne.core.Tables
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class ListReactionRolesCommand {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            val noPermissionEmbed = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("No permission")
                .setDescription("You don't have permission for this command!")
                .addField(MessageEmbed.Field("Permission", "Administrator", false))
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(noPermissionEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }

            return
        }

        var channelId = ""
        var message = ""
        var emoji = ""
        var roleMention = ""

        transaction {
            Tables.Reactions.select { Tables.Reactions.guild eq event.guild.idLong }.firstOrNull {
                event.guild.getGuildChannelById(it[Tables.Reactions.channel])?.let { channel -> channelId = channel.id }
                message = it[Tables.Reactions.message].toString()
                emoji = if (it[Tables.Reactions.emoji].matches("-?\\d+(\\.\\d+)?".toRegex())) event.guild.getEmoteById(it[Tables.Reactions.emoji])?.asMention.toString() else it[Tables.Reactions.emoji]
                event.guild.getRoleById(it[Tables.Reactions.role])?.let { role -> roleMention = role.asMention }

                true
            }
        }

        if (message == "" && channelId == "") {
            val nullEmbed = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("None")
                .setDescription("There are no reaction roles in this guild")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(nullEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }
            return
        }

        val listEmbed = EmbedBuilder()
            .setColor(Color.ORANGE)
            .setTitle("Reaction roles")
            .setDescription("This is the description of one reaction role in the list of this guild")
            .addField("Channel", if (channelId != "") "<#$channelId>" else "Unknown channel", false)
            .addField("Message ID", message, false)
            .addField("Emoji", if (emoji != "null") emoji else "Unknown emoji", false)
            .addField("Role", if (roleMention == "") "Unknown role" else roleMention, false)
            .addField("Index", "1", false)
            .setFooter(event.author.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.channel.sendMessage(listEmbed).queue { embedMessage ->
            embedMessage.addReaction("⏪").queue()
            embedMessage.addReaction("⏹").queue()
            embedMessage.addReaction("⏩").queue()
            embedMessage.addReaction("🗑").queue()
        }
    }
}