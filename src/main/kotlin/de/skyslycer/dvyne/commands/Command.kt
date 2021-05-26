package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

class Command(private val prefix: String, private val gitHubUrl: String) : ListenerAdapter() {
    private val clearCommand = ClearCommand(prefix)
    private val dmCommand = DmCommand(prefix)
    private val nukeCommand = NukeCommand()
    private val pingCommand = PingCommand()
    private val rpsCommand = RPSCommand(prefix)
    private val embedCommand = EmbedCommand(prefix)
    private val addRoleCommand = AddRoleCommand(prefix)
    private val removeRoleCommand = RemoveRoleCommand(prefix)
    private val reactionRoleCommand = ReactionRoleCommand(prefix)
    private val removeReactionRoleCommand = RemoveReactionRoleCommand(prefix)
    private val listReactionRolesCommand = ListReactionRolesCommand()

    private val commandsList = arrayListOf(
        "`$prefix dm @User (message)` - Send a user a direct message",
        "`$prefix delete (1-100)` - Delete certain messages",
        "`$prefix nuke` - Recreate the current channel",
        "`$prefix ping` - Sends the ping of the bot",
        "`$prefix embed #color title | description` - Create an embed sent by the bot",
        "`$prefix addrole @User @Role` - Gives a member a role",
        "`$prefix removerole @User @Role` - Removes a role from a member",
        "`$prefix reactionrole #channel (messageID) (emoji/emote) @Role` - Add a reaction role to a message",
        "`$prefix reactionrole [#channel] [messageID] (emoji/emote) [@Role]` - Remove a reaction role from the server",
        "`$prefix listreactionroles` - List all reaction roles of this server",
        "`$prefix rps (rock, paper, scissors)` - Play a game with the bot"
    ).joinToString("\n")

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")
        val isBotMention = args[0] == "<@!844492408162091008>"

        if (args[0].equals(prefix, true) || args[0] == "<@!844492408162091008>") {
            event.message.delete().queue()

            if (args.count() >= 2) {
                when(args[1].lowercase(Locale.getDefault())) {
                    "clear", "delete", "c" -> { clearCommand.onGuildMessageReceived(event); return }
                    "dm", "directmessage", "pm", "privatemessage" -> { dmCommand.onGuildMessageReceived(event, isBotMention); return }
                    "nuke", "recreate", "n" -> { nukeCommand.onGuildMessageReceived(event); return }
                    "ping", "p" -> { pingCommand.onGuildMessageReceived(event); return }
                    "rps", "rockpaperscissors" -> { rpsCommand.onGuildMessageReceived(event); return }
                    "embed", "e" -> { embedCommand.onGuildMessageReceived(event); return }
                    "addrole" -> { addRoleCommand.onGuildMessageReceived(event, isBotMention); return }
                    "removerole" -> { removeRoleCommand.onGuildMessageReceived(event, isBotMention); return }
                    "reactionrole", "rr" -> { reactionRoleCommand.onGuildMessageReceived(event); return }
                    "removereactionrole", "rrr" -> { removeReactionRoleCommand.onGuildMessageReceived(event); return }
                    "listreactionroles", "lrr" -> { listReactionRolesCommand.onGuildMessageReceived(event); return }
                    "help", "h" -> { sendHelp(event); return }
                }
            }

            sendHelp(event)
        }
    }

    private fun sendHelp(event: GuildMessageReceivedEvent) {
        val infoEmbed = EmbedBuilder()
            .setTitle("Dvyne Bot - Help")
            .setColor(Color.decode("#FDC901"))
            .setDescription("Thanks for using Dvyne!")
            .addField(MessageEmbed.Field("Commands", commandsList, false))
            .addField("OpenSource", "I'm open-source!\nContribute at: \n$gitHubUrl", false)
            .setFooter(event.author.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.channel.sendMessage(infoEmbed).queue { message ->
            message.addReaction("🗑").queue()
        }
    }
}