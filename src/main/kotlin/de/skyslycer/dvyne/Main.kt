package de.skyslycer.dvyne

import de.skyslycer.dvyne.commands.Command
import de.skyslycer.dvyne.commands.slashcommands.SlashCommand
import de.skyslycer.dvyne.core.Tables
import de.skyslycer.dvyne.listeners.AddReactionListener
import de.skyslycer.dvyne.listeners.ButtonClickListener
import de.skyslycer.dvyne.listeners.RemoveReactionListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File


fun main() {
    Database.connect("jdbc:sqlite:src/main/resources/dvyne.db", "org.sqlite.JDBC")

    val prefix = "d!"
    val gitHubUrl = "https://github.com/Skyslycer/DvyneBot"

    val jda = JDABuilder.createDefault(File("src/main/resources/token.txt").readText())
        .setActivity(Activity.listening("d! or @Dvyne"))
        .addEventListeners(Command(prefix, gitHubUrl))
        .addEventListeners(AddReactionListener())
        .addEventListeners(RemoveReactionListener())
        .addEventListeners(SlashCommand())
        .addEventListeners(ButtonClickListener())
        .build()

    transaction {
        SchemaUtils.createMissingTablesAndColumns(Tables.Reactions)
    }

    jda.awaitReady()

//    val commands = jda.updateCommands()
    val commands = jda.getGuildById(845327441458102324)!!.updateCommands()

    commands.addCommands(CommandData("ping", "Sends the ping of the bot"))

    commands.addCommands(CommandData("nuke", "Recreates the current channel"))

    commands.addCommands(CommandData("rps", "Play rock paper scissors with the bot")
        .addOption(OptionType.STRING, "rock-paper-scissors", "This is your input to play against the bot", true))

    commands.addCommands(CommandData("dm", "Send a direct message to a user")
        .addOption(OptionType.USER, "member", "The member you want to send the message", true)
        .addOption(OptionType.STRING, "message", "The message you want to send", true))

    commands.addCommands(CommandData("addrole", "Give a member a role")
        .addOption(OptionType.USER, "member", "The member you want to give the role", true)
        .addOption(OptionType.ROLE, "role", "The role you want to give the member", true))

    commands.addCommands(CommandData("clear", "Delete a number of messages in the channel (1-100)")
        .addOption(OptionType.INTEGER, "count", "Define how many messages should be deleted (1-100)", true))

    commands.addCommands(CommandData("embed", "Create an embed and send it in the current channel")
        .addOption(OptionType.STRING, "color", "The color the embed should have", true)
        .addOption(OptionType.STRING, "title", "The title the embed should have", true)
        .addOption(OptionType.STRING, "content", "The content that should be in the embed", true))

    commands.addCommands(CommandData("reactionrole", "Manage the reaction roles on this server")
        .addSubcommand(SubcommandData("add", "Add a reaction role to the server")
            .addOption(OptionType.CHANNEL, "channel", "The channel to send", true)
            .addOption(OptionType.STRING, "message-id", "The message id the reaction role should be applied to", true)
            .addOption(OptionType.STRING, "emoji", "The emoji that should trigger the reaction role", true)
            .addOption(OptionType.ROLE, "role", "The role a member should receive when reacting to the emoji", true))
        .addSubcommand(SubcommandData("remove", "Deletes a reaction role. At least one argument is required")
            .addOption(OptionType.CHANNEL, "channel", "When it is standalone it will remove all reaction roles from the channel")
            .addOption(OptionType.ROLE, "role", "When it is standalone it will remove all reaction roles with the role")
            .addOption(OptionType.STRING, "message-id", "When it is standalone it will remove all reaction roles with the message id")
            .addOption(OptionType.CHANNEL, "emoji", "Can not be standalone, but can be used when all the other arguments are set. Specifies the emoji"))
        .addSubcommand(SubcommandData("list", "List all reaction roles from this server"))
    )

    commands.addCommands(CommandData("removerole", "Remove a role from a member")
        .addOption(OptionType.USER, "member", "The member that should get the role removed", true)
        .addOption(OptionType.ROLE, "role", "The role that the member should get removed", true))

    commands.queue()
}