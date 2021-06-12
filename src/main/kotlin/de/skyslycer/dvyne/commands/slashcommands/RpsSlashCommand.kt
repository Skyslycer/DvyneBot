package de.skyslycer.dvyne.commands.slashcommands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.button.Button
import java.awt.Color
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class RpsSlashCommand {
    fun onSlashCommandEvent(event: SlashCommandEvent) {
        val playerInput = when (event.getOption("rock-paper-scissors")!!.asString) {
            "rock", "r" -> 3
            "paper", "p" -> 1
            "scissors", "s" -> 2
            else -> {
                event.reply("You can only use one of the following arguments: **rock - paper - scissors**")
                    .setEphemeral(true).queue()
                return
            }
        }

        val botInput = ThreadLocalRandom.current().nextInt(1, 4);

        val embedBuilder = EmbedBuilder()
            .setTitle("Rock, Paper, Scissors")
            .addField(MessageEmbed.Field("Your input", event.getOption("rock-paper-scissors")!!.asString, false))
            .addField(MessageEmbed.Field("Bot input", getInputNameFromInt(botInput), false))
            .setFooter(event.user.asTag)
            .setTimestamp(Instant.from(ZonedDateTime.now()))

        event.reply(MessageBuilder(setGameResult(playerInput, botInput, embedBuilder)).build())
            .addActionRow(Button.danger("${event.user.id}:delete", Emoji.ofUnicode("🗑")))
            .queue()
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