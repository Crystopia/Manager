package me.jesforge.proxymanager.commands

import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.checkerframework.checker.units.qual.mm

class NetworkCommand {
    val mm = MiniMessage.miniMessage()

    val command = commandTree("network") {
        literalArgument("reload") {
            executes(CommandExecutor { commandSender, commandArguments ->
                ConfigManager.save()
                commandSender.sendMessage(mm.deserialize("<color:#c3ffbf>All data has been reloaded.</color>"))

            })
        }
        literalArgument("version") {
            stringArgument("version") {
                executes(CommandExecutor { commandSender, commandArguments ->
                    ConfigManager.settings.serverData.version = commandArguments[0] as String
                    ConfigManager.save()

                    commandSender.sendMessage(mm.deserialize("<color:#9cffb1>The version is now updated</color>"))
                })
            }
        }
        literalArgument("playerLimit") {
            integerArgument("limit") {
                executes(CommandExecutor { commandSender, commandArguments ->
                    ConfigManager.settings.serverData.playerLimit = commandArguments[0] as Int
                    ConfigManager.save()

                    commandSender.sendMessage(mm.deserialize("<color:#faffc9>The number of network players is now ${commandArguments[0]}</color>"))
                })
            }
        }
    }

}