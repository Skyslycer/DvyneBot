package de.skyslycer.dvyne.commands

import de.skyslycer.dvyne.core.Tables
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class ReactionRoleCommand(private val prefix: String) {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

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

        if (args.count() == 6) {
            val channel = if (event.message.mentionedChannels.isEmpty()) {
                sendNullArgs(event)
                return
            } else event.message.mentionedChannels[0]
            val emoji: String = getEmoteOrEmoji(event.message, args[4])
            val role = if (event.message.mentionedRoles.isEmpty()) {
                sendNullArgs(event)
                return
            } else event.message.mentionedRoles[0]

            channel.retrieveMessageById(args[3])
                .flatMap {
                    if (event.message.emotes.isEmpty()) it.addReaction(emoji) else
                            (event.guild.getEmoteById(emoji)?.let { emote -> it.addReaction(emote) })
                }
                .queue({
                    transaction {
                        Tables.Reactions.insertIgnore {
                            it[this.message] = args[3].toLong()
                            it[this.channel] = channel.idLong
                            it[this.guild] = event.guild.idLong
                            it[this.role] = role.idLong
                            it[this.emoji] = emoji
                        }
                    }

                    val successEmbed = EmbedBuilder()
                        .setTitle("Success!")
                        .setColor(Color.GREEN)
                        .setDescription("Successfully added the reaction role with your arguments!")
                        .addField(MessageEmbed.Field("Channel", channel.asMention, false))
                        .addField(MessageEmbed.Field("Message ID", args[3], false))
                        .addField(MessageEmbed.Field("Emoji", if (event.message.emotes.isEmpty()) emoji else event.guild.getEmoteById(emoji)?.asMention, false))
                        .addField(MessageEmbed.Field("Role", role.asMention, false))
                        .setFooter(event.author.asTag)
                        .setTimestamp(Instant.from(ZonedDateTime.now()))
                        .build()

                    event.channel.sendMessage(successEmbed).queue { embedMessage ->
                        embedMessage.addReaction("🗑").queue()
                    }
                }) {
                    sendNullArgs(event)
                }
        } else {
            val wrongUsageEmbed = EmbedBuilder()
                .setTitle("Wrong usage!")
                .setColor(Color.ORANGE)
                .setDescription("Please use `$prefix reactionrole #channel (messageID) (emoji/emote) @Role`!")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(wrongUsageEmbed).queue { message ->
                message.addReaction("🗑").queue()
            }
        }
    }

    private fun getEmoteOrEmoji(message: Message, arg: String?): String {
        val emotes = message.emotes
        return if (emotes.isEmpty()) {
            arg.toString()
        } else {
            emotes[0].id
        }
    }

    private fun sendNullArgs(event: GuildMessageReceivedEvent) {
        val nullEmbed = EmbedBuilder()
            .setTitle("Unknown arguments!")
            .setColor(Color.RED)
            .setDescription("All of your arguments must be from this server and accessible for me!")
            .setFooter(event.author.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.channel.sendMessage(nullEmbed).queue { message ->
            message.addReaction("🗑").queue()
        }
    }
}