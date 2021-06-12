package de.skyslycer.dvyne.commands.slashcommands

import de.skyslycer.dvyne.core.Tables
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.button.Button
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class ListReactionRolesSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient permission! **Permission:** ADMINISTRATOR").setEphemeral(true).queue()
            return
        }

        var channelId = ""
        var message = ""
        var emoji = ""
        var roleMention = ""

        transaction {
            Tables.Reactions.select { Tables.Reactions.guild eq event.guild!!.idLong }.firstOrNull {
                event.guild!!.getGuildChannelById(it[Tables.Reactions.channel])?.let { channel -> channelId = channel.id }
                message = it[Tables.Reactions.message].toString()
                emoji = if (it[Tables.Reactions.emoji].matches("-?\\d+(\\.\\d+)?".toRegex())) event.guild!!.getEmoteById(it[Tables.Reactions.emoji])?.asMention.toString() else it[Tables.Reactions.emoji]
                event.guild!!.getRoleById(it[Tables.Reactions.role])?.let { role -> roleMention = role.asMention }

                true
            }
        }

        if (message == "" && channelId == "") {
            val nullEmbed = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("None")
                .setDescription("There are no reaction roles in this guild")
                .setFooter(event.user.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.reply(MessageBuilder(nullEmbed).build())
                .addActionRow(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))
                .queue()
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
            .setFooter(event.user.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.reply(MessageBuilder(listEmbed).build())
            .addActionRow(
                Button.primary("${event.user.id}:reactionroles_previous:1", "Previous"),
                Button.primary("${event.user.id}:reactionroles_stop", "Stop"),
                Button.primary("${event.user.id}:reactionroles_next:1", "Next"),
                Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑"))
            )
            .queue()
    }
}