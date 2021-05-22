package de.skyslycer.dvyne.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class AddReactionListener : ListenerAdapter() {
    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        var message: Message? = event.reaction.channel.retrieveMessageById(event.messageIdLong).complete() ?: return

        if (message!!.author.asTag == "Dvyne#5076" && message.embeds.isNotEmpty()) {
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

            } else if (event.member.user.asTag != "Dvyne#5076") {
                event.reaction.removeReaction(event.user).queue()
            }
        }
    }
}
