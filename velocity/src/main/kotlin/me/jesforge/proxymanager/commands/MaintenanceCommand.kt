package me.jesforge.proxymanager.commands

import com.velocitypowered.api.proxy.ConnectionRequestBuilder
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.booleanArgument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.MaintenanceServerData
import me.jesforge.proxymanager.config.ServerData
import net.kyori.adventure.text.minimessage.MiniMessage
import java.io.ObjectInputFilter.Config

class MaintenanceCommand {
    val mm = MiniMessage.miniMessage()
    val data = ConfigManager.settings.maintenance


    val command = commandTree("maintenance") {
        withPermission("crystopia.commands.maintenance")
        literalArgument("on") {
            executes(CommandExecutor { commandSender, commandArguments ->
                if (data.maintenance == true) {
                    commandSender.sendMessage(mm.deserialize("<color:#c1ff82>Remember that the maintenance work is already active!</color>"))
                    return@CommandExecutor
                } else {
                    ConfigManager.settings.maintenance.maintenance = true
                    ConfigManager.save()

                    Main.instance.server.allPlayers.forEach { player ->
                        if (player.hasPermission(ConfigManager.settings.maintenance.bypassPermission)) {

                        } else {
                            player.disconnect(
                                mm.deserialize(
                                    "<color:#ffe8a8>Hey <b><color:#e5ffc4>${player.username}</color></b>, our network is currently undergoing maintenance work. \n" + "We are working for your gaming experience. \nPlease wait until we are do</color>\n" + "\nOn our <color:#66bfff>#discord</color> <gray>(ʜᴛᴛᴘѕ://ᴄʀʏѕᴛᴏᴘɪᴀ.ʟɪɴᴋ/ᴅɪѕᴄᴏʀᴅ)</gray>  \nyou can get more information about \nthe current status of the maintenance work.\n" + "\n\n" + "\n<gray>---------------</gray>\n<color:#d8d4ff>ʀᴏʙɪᴛ ɪѕ ᴛʜɪɴᴋɪɴɢ...</color>"
                                )
                            )
                        }
                    }

                    commandSender.sendMessage(mm.deserialize("<color:#cbffbd>Maintenance work is now active!</color>"))

                }
            })

        }
        literalArgument("off") {
            executes(CommandExecutor { commandSender, commandArguments ->
                if (data.maintenance == false) {
                    commandSender.sendMessage(mm.deserialize("<color:#c1ff82>Remember that the maintenance work is already deactivate!</color>"))
                    return@CommandExecutor
                } else {
                    ConfigManager.settings.maintenance.maintenance = false
                    ConfigManager.save()

                    commandSender.sendMessage(mm.deserialize("<color:#cbffbd>Maintenance work is now inactive!</color>"))

                }
            })

        }
        literalArgument("server") {
            literalArgument("list") {
                executes(CommandExecutor { commandSender, commandArguments ->
                    commandSender.sendMessage(
                        mm.deserialize(
                            "<color:#fffcb0>Current Server</color>\n" + ConfigManager.settings.maintenance.maintenanceServer.map { server ->
                                "<gray>Name: " + server.value.name + " Permission: " + server.value.permission + " Enabled: " + server.value.enabled + "</gray>"
                            })
                    )
                })
            }
            literalArgument("manager") {
                stringArgument("name") {
                    replaceSuggestions(ArgumentSuggestions.strings {
                        Main.instance.server.allServers.map { server ->
                            server.serverInfo.name
                        }.toTypedArray()
                    })
                    booleanArgument("isMaintenance") {
                        executes(CommandExecutor { commandSender, commandArguments ->
                            ConfigManager.settings.maintenance.maintenanceServer[commandArguments[0] as String] =
                                MaintenanceServerData(
                                    name = commandArguments[0] as String,
                                    permission = "crystopia.server.maintenance.${commandArguments[0]}",
                                    enabled = commandArguments[1] as Boolean,
                                )
                            ConfigManager.save()

                            val server = Main.instance.server.getServer(commandArguments[0] as String)

                            server.get().playersConnected.forEach { player ->
                                player.createConnectionRequest(
                                    Main.instance.server.getServer(ConfigManager.settings.serverData.defaultServer.toString())
                                        .get()
                                ).fireAndForget()
                                player.sendMessage(mm.deserialize("<color:#f9ffba>This server is now in maintenance mode. You have been moved to the lobby and can join again later.</color>"))
                            }

                            commandSender.sendMessage(mm.deserialize("<color:#9cffb1>Der server wurde nun bearbeitet!</color>"))
                        })
                    }
                }
            }
        }

    }

}