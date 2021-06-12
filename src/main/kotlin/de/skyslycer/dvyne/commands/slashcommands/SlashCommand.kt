package de.skyslycer.dvyne.commands.slashcommands

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class SlashCommand : ListenerAdapter() {
    private val pingSlashCommand = PingSlashCommand()
    private val clearSlashCommand = ClearSlashCommand()
    private val nukeSlashCommand = NukeSlashCommand()
    private val rpsSlashCommand = RpsSlashCommand()
    private val addRoleSlashCommand = AddRoleSlashCommand()
    private val removeRoleSlashCommand = RemoveRoleSlashCommand()
    private val embedSlashCommand = EmbedSlashCommand()
    private val dmSlashCommand = DmSlashCommand()
    private val listReactionRolesSlashCommand = ListReactionRolesSlashCommand()
    private val addReactionRoleSlashCommand = AddReactionRoleSlashCommand()

    override fun onSlashCommand(event: SlashCommandEvent) {
        if (event.guild == null) return

        when(event.name) {
            "ping" -> pingSlashCommand.onSlashCommandEvent(event)
            "clear" -> clearSlashCommand.onSlashCommandEvent(event)
            "nuke" -> nukeSlashCommand.onSlashCommandEvent(event)
            "rps" -> rpsSlashCommand.onSlashCommandEvent(event)
            "addrole" -> addRoleSlashCommand.onSlashCommandEvent(event)
            "removerole" -> removeRoleSlashCommand.onSlashCommandEvent(event)
            "embed" -> embedSlashCommand.onSlashCommandEvent(event)
            "dm" -> dmSlashCommand.onSlashCommandEvent(event)
            "reactionrole" -> when(event.subcommandName) {
                "list" -> listReactionRolesSlashCommand.onSlashCommandEvent(event)
                "add" -> addReactionRoleSlashCommand.onSlashCommandEvent(event)
            }
            else -> event.reply("I could not handle this interaction :C").setEphemeral(true).queue()
        }
    }
}