package de.skyslycer.dvyne.commands.slashcommands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.button.Button
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class RemoveRoleSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        if (!event.member!!.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("Insufficient permission! **Permission:** MANAGE_ROLES").setEphemeral(true).queue()
            return
        }

        if (event.getOption("role")!!.asRole == event.guild?.publicRole) {
            event.reply("You can not remove the role **@everyone / @here** from a member!").setEphemeral(true).queue()
            return
        }

        if (event.getOption("role")!!.asRole == event.guild?.boostRole) {
            event.reply("You can not remove the **boost role** from a member!").setEphemeral(true).queue()
            return
        }

        if (event.getOption("role")!!.asRole == event.guild?.botRole) {
            event.reply("You can not remove a **bot role** from a member!").setEphemeral(true).queue()
            return
        }

        if (event.member?.let { event.guild!!.getMember(event.jda.selfUser)!!.canInteract(it) } != true) {
            event.guild!!.addRoleToMember(event.getOption("member")!!.asMember!!, event.getOption("role")!!.asRole)

            val successEmbed = EmbedBuilder()
                .setTitle("Success!")
                .setColor(Color.GREEN)
                .setDescription("Successfully removed the role ${event.getOption("role")!!.asRole.asMention} from the member ${event.getOption("member")!!.asMember!!.asMention}")
                .setFooter(event.user.asTag)
                .setTimestamp(Instant.from(ZonedDateTime.now()))
                .build()

            event.reply(MessageBuilder(successEmbed).build())
                .addActionRow(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))
                .queue()
        } else {
            event.reply("This user is too powerful for me or I have a lower role than the mentioned role :/").setEphemeral(true).queue()
        }
    }
}