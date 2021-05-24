package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class EmbedCommand(private val prefix: String) {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

        if (!event.member!!.hasPermission(Permission.MANAGE_WEBHOOKS)) {
            val noPermissionEmbed = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("No permission")
                .setDescription("You don't have permission for this command!")
                .addField(MessageEmbed.Field("Permission", "Manage webhooks", false))
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(noPermissionEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }

            return
        }

        if (args.count() >= 6) {

            try {
                val embedContents = args.subList(3, args.count()).joinToString(" ")

                val embed = EmbedBuilder()
                    .setFooter(event.author.asTag)
                    .setTimestamp(Instant.from(ZonedDateTime.now()))
                    .setColor(Color.decode(args[2]))
                    .setTitle(embedContents.split(" | ")[0])
                    .setDescription(embedContents.split(" | ")[1])
                    .build()

                event.channel.sendMessage(embed).queue()
            } catch (exception: IndexOutOfBoundsException) {
                sendWrongUsage(event)
            } catch (exception: NumberFormatException) {
                sendWrongUsage(event)
            }

        } else {
            sendWrongUsage(event)
        }
    }

    private fun sendWrongUsage(event: GuildMessageReceivedEvent) {
        val wrongUsageEmbed = EmbedBuilder()
            .setTitle("Wrong usage!")
            .setColor(Color.ORANGE)
            .setDescription("Please use `$prefix embed #color title | text`!")
            .setFooter(event.author.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.channel.sendMessage(wrongUsageEmbed).queue { embedMessage ->
            embedMessage.addReaction("🗑").queue()
        }
    }
}