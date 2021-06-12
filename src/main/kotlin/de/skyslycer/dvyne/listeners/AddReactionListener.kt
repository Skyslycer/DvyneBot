package de.skyslycer.dvyne.listeners

import de.skyslycer.dvyne.core.Tables
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class AddReactionListener : ListenerAdapter() {
    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        val message: Message = event.reaction.channel.retrieveMessageById(event.messageIdLong).complete() ?: return

        val roleId = transaction {
            Tables.Reactions.select { (Tables.Reactions.guild eq event.guild.idLong) and
                    (Tables.Reactions.channel eq event.channel.idLong) and
                    (Tables.Reactions.message eq event.reaction.messageIdLong) and
                    (Tables.Reactions.emoji eq if (event.reactionEmote.isEmoji) event.reactionEmote.emoji else event.reactionEmote.emote.id) }.firstOrNull()
                ?.get(Tables.Reactions.role)
        }

        if (roleId != null && event.user != event.jda.selfUser) {
            event.guild.getRoleById(roleId).let {
                if (it != null) {
                    event.guild.addRoleToMember(event.member, it).queue()
                } else {
                    transaction { Tables.Reactions.deleteWhere { (Tables.Reactions.role eq roleId) and (Tables.Reactions.guild eq event.guild.idLong) } }
                }
            }
        }

        if (message.author.asTag == "Dvyne#5076" && message.embeds.isNotEmpty()) {
            if (event.user.asTag == message.embeds[0].footer?.text ?: "null"
                && event.member.user.asTag != "Dvyne#5076"
                && message.embeds[0].description.equals("This is the description of one reaction role in the list of this guild")) {

                event.reaction.removeReaction(event.user).queue()

                when(event.reactionEmote) {
                    MessageReaction.ReactionEmote.fromUnicode("⏪", event.jda) -> generateNewListEmbed((message.embeds[0].fields[4].value?.toInt() ?: 2) - 1, message, event)
                    MessageReaction.ReactionEmote.fromUnicode("⏩", event.jda) -> generateNewListEmbed((message.embeds[0].fields[4].value?.toInt() ?: 2) + 1, message, event)
                        MessageReaction.ReactionEmote.fromUnicode("⏹", event.jda) -> {
                        message.removeReaction("⏪").queue()
                        message.removeReaction("⏹").queue()
                        message.removeReaction("⏩").queue()
                    }
                }
            }

            if (event.user.asTag == message.embeds[0].footer?.text ?: "null" && event.reactionEmote == MessageReaction.ReactionEmote.fromUnicode("🗑", event.jda) && event.member.user.asTag != "Dvyne#5076") {
                message.delete().queue()

            } else if (event.user.asTag == message.embeds[0].footer?.text ?: "null" &&
                event.reactionEmote == MessageReaction.ReactionEmote.fromUnicode("✅", event.jda) &&
                event.member.user.asTag != "Dvyne#5076"
                && message.embeds[0].title.equals("Nuke")) {

                message.textChannel.createCopy(message.guild).setPosition(message.textChannel.position).queue {
                    message.textChannel.delete().queue()

                    val confirmEmbed = EmbedBuilder()
                        .setTitle("Nuke successful!")
                        .setColor(Color.GREEN)
                        .setDescription("Successfully recreated the channel ${it.asMention}!")
                        .setFooter(event.user.asTag)
                        .setTimestamp(Instant.from(ZonedDateTime.now()))
                        .build()
                    it.sendMessage(confirmEmbed).queue { message ->
                        message.addReaction("🗑").queue()
                    }
                }

            } else if (event.user.asTag == message.embeds[0].footer?.text ?: "null" &&
                event.reactionEmote == MessageReaction.ReactionEmote.fromUnicode("❌", event.jda) &&
                event.member.user.asTag != "Dvyne#5076"
                && message.embeds[0].title.equals("Nuke")) {

                message.delete().queue()

            }
        }
    }

    private fun generateNewListEmbed(newIndex: Int, message: Message, event: GuildMessageReactionAddEvent) {
        var channelId = ""
        var messageId = ""
        var emoji = ""
        var roleMention = ""

        transaction {
            Tables.Reactions.select { Tables.Reactions.guild eq event.guild.idLong }.drop(if (newIndex == 0) newIndex else newIndex - 1).firstOrNull() {
                event.guild.getGuildChannelById(it[Tables.Reactions.channel])?.let { channel -> channelId = channel.id }
                messageId = it[Tables.Reactions.message].toString()
                emoji = if (it[Tables.Reactions.emoji].matches("-?\\d+(\\.\\d+)?".toRegex())) event.guild.getEmoteById(it[Tables.Reactions.emoji])?.asMention.toString() else it[Tables.Reactions.emoji]
                event.guild.getRoleById(it[Tables.Reactions.role])?.let { role -> roleMention = role.asMention }

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
            .addField("Index", newIndex.toString(), false)
            .setFooter(event.user.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        message.editMessage(listEmbed).override(true).queue()
    }
}
