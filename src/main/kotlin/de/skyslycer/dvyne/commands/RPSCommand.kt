package de.skyslycer.dvyne.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

class RPSCommand(private val prefix: String) {
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ")

        if (args.count() != 3) {
            sendWrongUsage(event)
            return
        }

        val playerInput = when (args[2]) {
            "rock" -> 3
            "paper" -> 1
            "scissors" -> 2
            else -> {
                sendWrongUsage(event); return
            }
        }

        val botInput = Random().nextInt(3) + 1

        var embedBuilder = EmbedBuilder()
            .setTitle("Rock, Paper, Scissors")
            .addField(MessageEmbed.Field("Your input", args[2], false))
            .addField(MessageEmbed.Field("Bot input", getInputNameFromInt(botInput), false))
            .setFooter(event.author.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))

        embedBuilder = setGameResult(playerInput, botInput, embedBuilder)

        event.channel.sendMessage(embedBuilder.build()).queue { message ->
            message.addReaction("🗑").queue()
        }
    }

    private fun sendWrongUsage(event: GuildMessageReceivedEvent) {
        val wrongUsageEmbed = EmbedBuilder()
            .setTitle("Wrong usage!")
            .setColor(Color.ORANGE)
            .setDescription("Please use `$prefix rps (rock, paper, scissors)`")
            .setFooter(event.author.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))
            .build()

        event.channel.sendMessage(wrongUsageEmbed).queue { message ->
            message.addReaction("🗑").queue()
        }
    }

    private fun getInputNameFromInt(input: Int): String {
        when (input) {
            1 -> return "paper"
            2 -> return "scissors"
            3 -> return "rock"
        }

        return "error"
    }

    private fun setGameResult(input: Int, botInput: Int, embedBuilder: EmbedBuilder): EmbedBuilder {
        when (input) {
            1 -> {
                when (botInput) {
                    2 -> {
                        embedBuilder.setColor(Color.RED)
                        embedBuilder.addField("Result", "You lost", false)
                    }
                    3 -> {
                        embedBuilder.setColor(Color.GREEN)
                        embedBuilder.addField("Result", "You won", false)
                    }
                    1 -> {
                        embedBuilder.setColor(Color.ORANGE)
                        embedBuilder.addField("Result", "Same input", false)
                    }
                }
            }
            2 -> {
                when (botInput) {
                    3 -> {
                        embedBuilder.setColor(Color.RED)
                        embedBuilder.addField("Result", "You lost", false)
                    }
                    1 -> {
                        embedBuilder.setColor(Color.GREEN)
                        embedBuilder.addField("Result", "You won", false)
                    }
                    2 -> {
                        embedBuilder.setColor(Color.ORANGE)
                        embedBuilder.addField("Result", "Same input", false)
                    }
                }
            }
            3 -> {
                when (botInput) {
                    1 -> {
                        embedBuilder.setColor(Color.RED)
                        embedBuilder.addField("Result", "You lost", false)
                    }
                    2 -> {
                        embedBuilder.setColor(Color.GREEN)
                        embedBuilder.addField("Result", "You won", false)
                    }
                    3 -> {
                        embedBuilder.setColor(Color.ORANGE)
                        embedBuilder.addField("Result", "Same input", false)
                    }
                }
            }
        }

        return embedBuilder
    }
}