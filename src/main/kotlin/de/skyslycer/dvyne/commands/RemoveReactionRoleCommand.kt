package de.skyslycer.dvyne.commands

import de.skyslycer.dvyne.core.Tables
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class RemoveReactionRoleCommand(private val prefix: String) {
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
                return
            } else event.message.mentionedChannels[0]
            val emoji: String = getEmoteOrEmoji(event.message, args[4])
            val role = if (event.message.mentionedRoles.isEmpty()) {
                return
            } else event.message.mentionedRoles[0]

            channel.retrieveMessageById(args[3])
                .flatMap {
                    if (event.message.emotes.isEmpty()) it.removeReaction(emoji, event.jda.selfUser) else (event.guild.getEmoteById(
                        emoji
                    )?.let { emote -> it.removeReaction(emote, event.jda.selfUser) })
                }
                .queue({
                    transaction {
                        Tables.Reactions.deleteWhere {
                            (Tables.Reactions.message eq args[3].toLong()) and
                                    (Tables.Reactions.channel eq channel.idLong) and
                                    (Tables.Reactions.guild eq event.guild.idLong) and
                                    (Tables.Reactions.role eq role.idLong) and
                                    (Tables.Reactions.emoji eq emoji)
                        }
                    }

                    val successEmbed = EmbedBuilder()
                        .setTitle("Success!")
                        .setColor(Color.GREEN)
                        .setDescription("Successfully removed the reaction role with your arguments!")
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
                    val nullEmbed = EmbedBuilder()
                        .setTitle("Invalid message/emoji")
                        .setDescription("Please enter a valid message/emoji!")
                        .setColor(Color.RED)
                        .setFooter(event.author.asTag)
                        .setTimestamp(Instant.from(ZonedDateTime.now()))
                        .build()

                    event.channel.sendMessage(nullEmbed).queue { embedMessage ->
                        embedMessage.addReaction("🗑").queue()
                    }
                }
        } else if (args.count() == 3 && args[2].equals("*", true)) {
            transaction {
                Tables.Reactions.deleteWhere {
                    Tables.Reactions.guild eq event.guild.idLong
                }
            }

            val successEmbed = EmbedBuilder()
                .setTitle("Success!")
                .setColor(Color.GREEN)
                .setDescription("Successfully removed all the reaction roles from this server!")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(successEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }
        }  else if (args.count() == 3 && event.message.mentionedRoles.isNotEmpty()) {
            transaction {
                Tables.Reactions.deleteWhere {
                    (Tables.Reactions.guild eq event.guild.idLong) and (Tables.Reactions.role eq event.message.mentionedRoles[0].idLong)
                }
            }

            val successEmbed = EmbedBuilder()
                .setTitle("Success!")
                .setColor(Color.GREEN)
                .setDescription("Successfully removed all the reaction roles with your arguments!")
                .addField("Role", event.message.mentionedRoles[0].asMention, false)
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(successEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }
        } else if (args.count() == 3 && event.message.mentionedChannels.isNotEmpty()) {
            transaction {
                Tables.Reactions.deleteWhere {
                    (Tables.Reactions.guild eq event.guild.idLong) and (Tables.Reactions.channel eq event.message.mentionedChannels[0].idLong)
                }
            }

            val successEmbed = EmbedBuilder()
                .setTitle("Success!")
                .setColor(Color.GREEN)
                .setDescription("Successfully removed all the reaction roles with your arguments!")
                .addField("Channel", event.message.mentionedChannels[0].asMention, false)
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(successEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }
        } else if (args.count() == 3) {
            if (!args[2].matches("-?\\d+(\\.\\d+)?".toRegex())) { sendWrongUsage(event); return }

            transaction {
                Tables.Reactions.deleteWhere {
                    (Tables.Reactions.guild eq event.guild.idLong) and (Tables.Reactions.message eq args[2].toLong())
                }
            }

            val successEmbed = EmbedBuilder()
                .setTitle("Success!")
                .setColor(Color.GREEN)
                .setDescription("Successfully removed all the reaction roles with your arguments!")
                .addField("Message ID", args[2], false)
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(successEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }
        } else {
            sendWrongUsage(event)
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

    private fun sendWrongUsage(event: GuildMessageReceivedEvent) {
        val wrongUsageEmbed = EmbedBuilder()
            .setTitle("Wrong usage!")
            .setColor(Color.ORANGE)
            .setDescription("Please use `$prefix reactionrole [#channel] [messageID] (emoji/emote) [@Role]`!")
            .setFooter(event.author.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.channel.sendMessage(wrongUsageEmbed).queue { message ->
            message.addReaction("🗑").queue()
        }
    }
}