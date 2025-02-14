package me.jesforge.proxymanager.commands

import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import net.kyori.adventure.text.minimessage.MiniMessage

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

    }

}