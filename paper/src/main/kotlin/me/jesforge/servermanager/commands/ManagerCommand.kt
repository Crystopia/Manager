package me.jesforge.servermanager.commands

import com.google.common.io.ByteStreams
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.*
import me.jesforge.servermanager.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Player


class ManagerCommand {

    val command = commandTree("manager") {
        literalArgument("kick") {
            stringArgument("player") {
                replaceSuggestions(
                    ArgumentSuggestions.strings {
                        Main.instance.server.onlinePlayers.map { it.name }.toTypedArray()
                    })
                textArgument("message") {
                    anyExecutor { commandSender, commandArguments ->
                        val playerName = commandArguments[0].toString()
                        val player = Bukkit.getOfflinePlayer(playerName).player!!

                        val out = ByteStreams.newDataOutput()
                        out.writeUTF("kick")
                        out.writeUTF(playerName)
                        out.writeUTF(commandArguments[1].toString())

                        player.sendPluginMessage(
                            Main.instance, "networkmanager:channel", out.toByteArray()
                        )
                    }
                }
            }
        }
        literalArgument("ban") {
            stringArgument("player") {
                replaceSuggestions(
                    ArgumentSuggestions.strings {
                        Main.instance.server.offlinePlayers.map { it.name }.toTypedArray()
                    })
                stringArgument("time") {
                    textArgument("message") {
                        anyExecutor { commandSender, commandArguments ->
                            val playerName = commandArguments[0].toString()

                            val out = ByteStreams.newDataOutput()
                            out.writeUTF("ban")
                            out.writeUTF(playerName)
                            out.writeUTF(commandArguments[1].toString())
                            out.writeUTF(commandArguments[2].toString())

                            Main.instance.server.onlinePlayers.first().sendPluginMessage(
                                Main.instance, "networkmanager:channel", out.toByteArray()
                            )
                        }
                    }
                }
            }

        }
    }
}