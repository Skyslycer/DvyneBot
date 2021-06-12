package de.skyslycer.dvyne.commands.slashcommands

import de.skyslycer.dvyne.core.Tables
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.button.Button
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class AddReactionRoleSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Insufficient permission! **Permission:** ADMINISTRATOR").setEphemeral(true).queue()
            return
        }

        var channel = event.getOption("channel")!!.asGuildChannel

        if (channel.type != ChannelType.TEXT) {
            event.reply("Your **channel** input must be a text channel!").setEphemeral(true).queue()
            return
        }

        channel = channel as TextChannel

        val emoteMatcher = Message.MentionType.EMOTE.pattern.matcher(event.getOption("emoji")!!.asString)

        val emoji = if (emoteMatcher.find()) {
            emoteMatcher.group(2).toString()
        } else {
            event.getOption("emoji")!!.asString
        }

        if (event.getOption("role")!!.asRole == event.guild?.publicRole) {
            event.reply("You can not use **@everyone / @here** as a reaction role!").setEphemeral(true).queue()
            return
        }

        if (event.getOption("role")!!.asRole == event.guild?.boostRole) {
            event.reply("You can not use the **boost role** as a reaction role!").setEphemeral(true).queue()
            return
        }

        if (event.getOption("role")!!.asRole == event.guild?.botRole) {
            event.reply("You can not use a **bot role** as a reaction role!").setEphemeral(true).queue()
            return
        }

        if (!event.guild!!.getMember(event.jda.selfUser)!!.canInteract(event.getOption("role")!!.asRole)) {
            event.reply("I can not manage the role **${event.getOption("role")!!.asRole.asMention}** because I do not have the permission\n" +
                "to **manage roles/members** or my role is under the mentioned role!").setEphemeral(true).queue()
            return
        }

        val role = event.getOption("role")!!.asRole

        channel.retrieveMessageById(event.getOption("message-id")!!.asString)
            .flatMap {
                if (emoji.matches("-?\\d+(\\.\\d+)?".toRegex())) event.guild!!.getEmoteById(emoji)
                    ?.let { it1 -> it.addReaction(it1) } else it.addReaction(emoji)
            }
            .queue({
                transaction {
                    Tables.Reactions.insertIgnore {
                        it[this.message] = event.getOption("message-id")!!.asLong
                        it[this.channel] = channel.idLong
                        it[this.guild] = event.guild!!.idLong
                        it[this.role] = role.idLong
                        it[this.emoji] = emoji
                    }
                }

                val successEmbed = EmbedBuilder()
                    .setTitle("Success!")
                    .setColor(Color.GREEN)
                    .setDescription("Successfully added the reaction role with your arguments!")
                    .addField(MessageEmbed.Field("Channel", channel.asMention, false))
                    .addField(MessageEmbed.Field("Message ID", event.getOption("message-id")!!.asString, false))
                    .addField(MessageEmbed.Field("Emoji", if (emoji.matches("-?\\d+(\\.\\d+)?".toRegex())) event.guild!!.getEmoteById(emoji)!!.asMention else emoji, false))
                    .addField(MessageEmbed.Field("Role", role.asMention, false))
                    .setFooter(event.user.asTag)
                    .setTimestamp(Instant.from(ZonedDateTime.now()))
                    .build()

                event.reply(MessageBuilder(successEmbed).build())
                    .addActionRow(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))
                    .queue()
            }) {
                event.reply("Your **message/emoji** input must be valid!").setEphemeral(true).queue()
            }
    }
}