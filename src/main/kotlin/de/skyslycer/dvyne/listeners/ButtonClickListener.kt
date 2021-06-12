package de.skyslycer.dvyne.listeners

import de.skyslycer.dvyne.core.Tables
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.ActionRow
import net.dv8tion.jda.api.interactions.button.Button
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class ButtonClickListener : ListenerAdapter() {
    override fun onButtonClick(event: ButtonClickEvent) {
        val id = event.componentId.split(":")

        if (id[0] != event.user.id) return

        event.deferEdit().queue()

        when(id[1]) {
            "delete" -> event.hook.deleteOriginal().queue()
            "nuke" -> {
                event.guild?.let {
                    event.textChannel.createCopy(it).setPosition(event.textChannel.position).queue { channel ->
                        event.textChannel.delete().queue()

                        val confirmEmbed = EmbedBuilder()
                            .setTitle("Nuke successful!")
                            .setColor(Color.GREEN)
                            .setDescription("Successfully recreated the channel ${channel.asMention}!")
                            .setFooter(event.user.asTag)
                            .setTimestamp(Instant.from(ZonedDateTime.now()))
                            .build()

                        val message = MessageBuilder(confirmEmbed).setActionRows(ActionRow.of(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))).build()

                        channel.sendMessage(message).queue()
                    }
                }
            }
            "reactionroles_next" -> generateNewListEmbed(id[2].toInt() + 1, event.message!!, event)
            "reactionroles_previous" -> generateNewListEmbed(id[2].toInt() + - 1, event.message!!, event)
            "reactionroles_stop" -> event.message!!.editMessage(MessageBuilder(event.message).setActionRows(ActionRow.of(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))).build()).override(true).queue()
        }
    }

    private fun generateNewListEmbed(newIndex: Int, message: Message, event: ButtonClickEvent) {
        var channelId = ""
        var messageId = ""
        var emoji = ""
        var roleMention = ""

        transaction {
            Tables.Reactions.select { Tables.Reactions.guild eq event.guild!!.idLong }.drop(if (newIndex == 0) newIndex else newIndex - 1).firstOrNull {
                event.guild!!.getGuildChannelById(it[Tables.Reactions.channel])?.let { channel -> channelId = channel.id }
                messageId = it[Tables.Reactions.message].toString()
                emoji = if (it[Tables.Reactions.emoji].matches("-?\\d+(\\.\\d+)?".toRegex())) event.guild!!.getEmoteById(it[Tables.Reactions.emoji])?.asMention.toString() else it[Tables.Reactions.emoji]
                event.guild!!.getRoleById(it[Tables.Reactions.role])?.let { role -> roleMention = role.asMention }

                true
            }
        }

        if (channelId == "" && messageId == "") return

        val listEmbed = EmbedBuilder()
            .setColor(Color.ORANGE)
            .setTitle("Reaction roles")
            .setDescription("This is the description of one reaction role in the list of this guild")
            .addField("Channel", if (channelId != "") "<#$channelId>" else "Unknown channel", false)
            .addField("Message ID", messageId, false)
            .addField("Emoji", if (emoji != "null") emoji else "Unknown emoji", false)
            .addField("Role", if (roleMention == "") "Unknown role" else roleMention, false)
            .setFooter(event.user.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        message.editMessage(listEmbed).override(true)
            .setActionRows(ActionRow.of(Button.primary("${event.user.id}:reactionroles_previous:$newIndex", "Previous"),
                Button.primary("${event.user.id}:reactionroles_stop", "Stop"),
                Button.primary("${event.user.id}:reactionroles_next:$newIndex", "Next"),
                Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑"))))
            .queue()
    }
}