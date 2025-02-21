package me.jesforge.servermanager.commands

import com.google.common.io.ByteStreams
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.*
import me.jesforge.servermanager.Main
import org.bukkit.entity.Player


class ManagerCommand {

    val command = commandTree("manager") {
        literalArgument("kick") {
            playerArgument("player") {
                textArgument("message") {
                    executes(CommandExecutor { commandSource, commandArguments ->
                        val out = ByteStreams.newDataOutput()
                        out.writeUTF("kick")
                        out.writeUTF((commandArguments[0] as Player).player!!.name)
                        out.writeUTF(commandArguments[1].toString())

                        (commandSource as Player).sendPluginMessage(
                            Main.instance, "networkmanager:channel", out.toByteArray()
                        )
                    })
                }
            }
        }
        literalArgument("ban") {
            playerArgument("player") {
                stringArgument("time") {
                    textArgument("message") {
                        executes(CommandExecutor { commandSource, commandArguments ->
                            val out = ByteStreams.newDataOutput()
                            out.writeUTF("ban")
                            out.writeUTF((commandArguments[0] as Player).player!!.name)
                            out.writeUTF(commandArguments[1].toString())
                            out.writeUTF(commandArguments[2].toString())

                            (commandSource as Player).sendPluginMessage(
                                Main.instance, "networkmanager:channel", out.toByteArray()
                            )
                        })
                    }
                }
            }

        }
    }
}