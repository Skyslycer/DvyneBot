package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class RemoveRoleCommand(private val prefix: String) {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

        if (!event.member!!.hasPermission(Permission.MANAGE_ROLES)) {
            val noPermissionEmbed = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("No permission")
                .setDescription("You don't have permission for this command!")
                .addField(MessageEmbed.Field("Permission", "Manage roles", false))
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(noPermissionEmbed).queue { embedMessage ->
                embedMessage.addReaction("🗑").queue()
            }

            return
        }

        if (args.count() != 4) {
            val wrongUsageEmbed = EmbedBuilder()
                .setTitle("Wrong usage!")
                .setColor(Color.ORANGE)
                .setDescription("Please use `$prefix removerole @User @Role`!")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(wrongUsageEmbed).queue { message ->
                message.addReaction("🗑").queue()
            }

            return
        }

        try {
            val mentionedUser = event.message.mentionedMembers[0]
            val mentionedRole = event.message.mentionedRoles[0]

            if (mentionedRole == null || mentionedUser == null) {
                throw IndexOutOfBoundsException("Null mention")
            }

            event.guild.removeRoleFromMember(mentionedUser, mentionedRole).queue()

            val successfulEmbed = EmbedBuilder()
                .setTitle("Success!")
                .setColor(Color.GREEN)
                .setDescription("Successfully removed the role ${mentionedRole.asMention} from the member ${mentionedUser.asMention}")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(successfulEmbed).queue { message ->
                message.addReaction("🗑").queue()
            }
        } catch (exception: IndexOutOfBoundsException) {
            val unknownMentionEmbed = EmbedBuilder()
                .setTitle("Invalid member/role")
                .setColor(Color.RED)
                .setDescription("Please mention a valid member/role!")
                .setFooter(event.author.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.channel.sendMessage(unknownMentionEmbed).queue { message ->
                message.addReaction("🗑").queue()
            }

        }
    }
}