package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class ClearCommand(private val prefix: String) {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

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

        if (args.count() == 3) {
            try {
                if (args[2].toInt() >= 100 || args[2].toInt() <= 1) {
                    val invalidNumberEmbed = EmbedBuilder()
                        .setTitle("Wrong usage!")
                        .setColor(Color.ORANGE)
                        .setDescription("You can only choose a number between 1 and 100!")
                        .setFooter(event.author.asTag)
                        .setTimestamp(Instant.from(ZonedDateTime.now()))
                        .build()

                    event.channel.sendMessage(invalidNumberEmbed).queue { message ->
                        message.addReaction("🗑").queue()
                    }

                    return
                }

                val messages: List<Message> = event.channel.history.retrievePast(args[2].toInt()).complete()

                if (messages.count() <= 1) {
                    val tooLowMessagesEmbed = EmbedBuilder()
                        .setTitle("Not enough messages!")
                        .setColor(Color.RED)
                        .setDescription("There must be at least 1 message to delete!")
                        .setFooter(event.author.asTag)
                        .setTimestamp(Instant.from(ZonedDateTime.now()))
                        .build()

                    event.channel.sendMessage(tooLowMessagesEmbed).queue { message ->
                        message.addReaction("🗑").queue()
                    }

                    return
                }

                event.channel.deleteMessages(messages).queue()

                val successEmbed = EmbedBuilder()
                    .setTitle("Success!")
                    .setColor(Color.GREEN)
                    .setDescription("You deleted the last ${messages.size} messages!")
                    .setFooter(event.author.asTag)
                    .setTimestamp(Instant.from(ZonedDateTime.now()))
                    .build()

                event.channel.sendMessage(successEmbed).queue { message ->
                    message.addReaction("🗑").queue()
                }
            } catch (exception: NumberFormatException) {
                val errorEmbed = EmbedBuilder()
                    .setTitle("Error")
                    .setColor(Color.RED)
                    .setDescription("Your 2nd argument needs to be a number!")
                    .setFooter(event.author.asTag)
                    .setTimestamp(Instant.from(ZonedDateTime.now()))
                    .build()

                event.channel.sendMessage(errorEmbed).queue { message ->
                    message.addReaction("🗑").queue()
                }
            }
        } else {
            val usageEmbed = EmbedBuilder()
                .setTitle("Wrong usage!")
                .setColor(Color.ORANGE)
                .setDescription("Please use `$prefix delete (1-100)`!")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(usageEmbed).queue { message ->
                message.addReaction("🗑").queue()
            }
        }
    }
}