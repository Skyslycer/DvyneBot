package de.skyslycer.dvyne.listeners

import de.skyslycer.dvyne.core.Tables
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class RemoveReactionListener : ListenerAdapter() {
    override fun onGuildMessageReactionRemove(event: GuildMessageReactionRemoveEvent) {
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
                    event.guild.removeRoleFromMember(event.member!!, it).queue()
                } else {
                    transaction { Tables.Reactions.deleteWhere { (Tables.Reactions.role eq roleId) and (Tables.Reactions.guild eq event.guild.idLong) } }
                }
            }
        }
    }
}