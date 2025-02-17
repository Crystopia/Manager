package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent
import com.velocitypowered.api.proxy.Player
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import net.kyori.adventure.text.minimessage.MiniMessage

class CommandManagerEvent {
    val mm = MiniMessage.miniMessage()

    @Subscribe(order = PostOrder.FIRST)
    fun onTab(event: PlayerAvailableCommandsEvent) {
        val player = event.player

        if (player.hasPermission(ConfigManager.commands.bypassPermissions)) {
            return
        }

        val serverName = player.currentServer.orElse(null)?.serverInfo?.name ?: "ALL"

        val allowedCommands = getAllowedTabCompleteFromPlayer(player, serverName)

        event.rootNode.children.removeIf { child ->
            val command = child.name.lowercase()
            !allowedCommands.contains(
                "/" + command.split(" ").first()
            )
        }
    }

    @Subscribe
    fun onCommand(event: CommandExecuteEvent) {
        val player = event.commandSource as? Player ?: return

        if (player.hasPermission(ConfigManager.commands.bypassPermissions)) {
            return
        }

        val serverName = player.currentServer.orElse(null)?.serverInfo?.name ?: "ALL"

        val allowedCommands = getAllowedCommandsForPlayer(player, serverName)


        val commandParts = event.command.split(" ")

        if (commandParts.isNotEmpty() && (allowedCommands.contains("/" + commandParts[0]) || (commandParts.size > 1 && allowedCommands.contains(
                "/" + commandParts[0] + " " + commandParts[1]
            )) || (commandParts.size > 2 && allowedCommands.contains("/" + commandParts[0] + " " + commandParts[1] + " " + commandParts[2])))
        ) {

            return
        } else {
            event.result = CommandExecuteEvent.CommandResult.denied()
            player.sendMessage(mm.deserialize(ConfigManager.commands.commandErrorMessage))
        }
    }

    private fun getAllowedTabCompleteFromPlayer(player: Player, serverName: String): Set<String> {
        val commandSettings = ConfigManager.commands
        val allCommands = commandSettings.servers["ALL"]?.permissions ?: emptyMap()
        val serverCommands = commandSettings.servers[serverName]?.permissions ?: emptyMap()

        val allowedCommands = mutableSetOf<String>()

        allCommands.forEach { (permission, commands) ->
            if (player.hasPermission(permission) || permission == "ALL") {
                commands.tabComplete.forEach { command ->
                    allowedCommands.add("/$command")
                }
            }
        }

        serverCommands.forEach { (permission, commands) ->
            if (player.hasPermission(permission)) {
                commands.tabComplete.forEach { command ->
                    allowedCommands.add("/$command")
                }
            }
        }

        return allowedCommands
    }

    private fun getAllowedCommandsForPlayer(player: Player, serverName: String): Set<String> {
        val commandSettings = ConfigManager.commands
        val allCommands = commandSettings.servers["ALL"]?.permissions ?: emptyMap()
        val serverCommands = commandSettings.servers[serverName]?.permissions ?: emptyMap()

        val allowedCommands = mutableSetOf<String>()

        allCommands.forEach { (permission, commands) ->
            if (player.hasPermission(permission) || permission == "ALL") {
                commands.command.forEach { command ->
                    allowedCommands.add("/$command")
                }
            }
        }

        serverCommands.forEach { (permission, commands) ->
            if (player.hasPermission(permission)) {
                commands.command.forEach { command ->
                    allowedCommands.add("/$command")
                }
            }
        }

        return allowedCommands
    }

}
