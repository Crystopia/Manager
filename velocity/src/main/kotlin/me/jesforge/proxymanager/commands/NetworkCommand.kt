package me.jesforge.proxymanager.commands

import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
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
    }

}