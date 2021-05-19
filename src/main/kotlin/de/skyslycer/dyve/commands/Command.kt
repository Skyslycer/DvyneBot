package de.skyslycer.dyve.commands

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Command(private val prefix: String, private val gitHubUrl: String) : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

        if (args[0].equals("prefix", true)) {
            if (args.count() > 1) {
                when(args[1].lowercase()) {
                    "clear" -> { ClearCommand(prefix).onGuildMessageReceived(event); return}
                }
            }
        }
    }
}