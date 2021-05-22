package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime

class Command(private val prefix: String, private val gitHubUrl: String) : ListenerAdapter() {
    private val clearCommand = ClearCommand(prefix)
    private val dmCommand = DmCommand(prefix)
    private val nukeCommand = NukeCommand()
    private val pingCommand = PingCommand()
    private val rpsCommand = RPSCommand(prefix)

    private val commandsList = arrayListOf(
        "`$prefix dm @User (message)` - Send a user a direct message",
        "`$prefix delete (1-100)` - Delete certain messages",
        "`$prefix nuke` - Recreate the current channel",
        "`$prefix ping` - Sends the ping of the bot",
        "`$prefix rps (rock, paper, scissors)` - Play a game with the bot"
    ).joinToString("\n")

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

        if (args[0].equals(prefix, true) || args[0] == "<@!844492408162091008>") {
            event.message.delete().queue()

            if (args.count() >= 2) {
                when(args[1].lowercase()) {
                    "clear", "delete" -> { clearCommand.onGuildMessageReceived(event); return }
                    "dm", "directmessage", "pm", "privatemessage" -> { dmCommand.onGuildMessageReceived(event); return }
                    "nuke", "recreate", "n" -> { nukeCommand.onGuildMessageReceived(event); return }
                    "ping", "p" -> { pingCommand.onGuildMessageReceived(event); return }
                    "rps", "rockpaperscissors" -> { rpsCommand.onGuildMessageReceived(event); return }
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