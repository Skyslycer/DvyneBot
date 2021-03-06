package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class DmCommand(private val prefix: String) {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent, isBotMention: Boolean) {
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

        if (args.count() >= 4) {

            if (event.message.mentionedUsers.isEmpty() || event.message.mentionedUsers[if (isBotMention) 1 else 0] == null || event.message.mentionedUsers[if (isBotMention) 1 else 0] == event.jda.selfUser) {
                val wrongUsageEmbed = EmbedBuilder()
                    .setTitle("Unknown user")
                    .setColor(Color.RED)
                    .setDescription("Please tag a valid member!")
                    .setFooter(event.author.asTag)
                    .setTimestamp(Instant.from(ZonedDateTime.now()))
                    .build()

                event.channel.sendMessage(wrongUsageEmbed).queue { message ->
                    message.addReaction("🗑").queue()
                }

                return
            }

            val user = event.message.mentionedUsers[if (isBotMention) 1 else 0]
            val message = arrayListOf("")

            for (i in 3 until args.count()) {
                message += args[i]
            }

            val dmEmbed = EmbedBuilder()
                .setTitle(event.guild.name)
                .setColor(Color.GREEN)
                .setDescription("You received a direct message from a Discord server!")
                .addField(MessageEmbed.Field("Message", message.joinToString(" "), false))
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            user.openPrivateChannel()
                .flatMap { it.sendMessage(dmEmbed) }
                .queue()

            val dmSuccessfulEmbed = EmbedBuilder()
                .setTitle("Message sent")
                .setColor(Color.GREEN)
                .setDescription("The user ${user.asMention} successfully received the message!")
                .addField(MessageEmbed.Field("Message", message.joinToString(" "), false))
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(dmSuccessfulEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }

        } else {
            val wrongUsageEmbed = EmbedBuilder()
                .setTitle("Wrong usage!")
                .setColor(Color.ORANGE)
                .setDescription("Please use `$prefix dm @User (message)`!")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(wrongUsageEmbed).queue { message ->
                message.addReaction("🗑").queue()
            }
        }
    }
}