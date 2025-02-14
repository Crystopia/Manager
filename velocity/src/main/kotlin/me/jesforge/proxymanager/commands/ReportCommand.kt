package me.jesforge.proxymanager.commands

import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.Player
import me.jesforge.proxymanager.config.PlayerData
import me.jesforge.proxymanager.config.PlayerReportData
import net.kyori.adventure.text.minimessage.MiniMessage
import java.time.Instant
import java.util.UUID

class ReportCommand {
    val mm = MiniMessage.miniMessage()

    val command = commandTree("report") {
        literalArgument("management") {
            withPermission("crystopia.command.report.management")
            literalArgument("list") {
                executes(CommandExecutor { sender, args ->
                    
                })
            }
        }
        playerArgument("player") {
            stringArgument("reason - Links to a file (imgnur.com) allowed!") {
                executes(CommandExecutor { commandSource, commandArguments ->
                    val player = commandArguments[0] as com.velocitypowered.api.proxy.Player
                    val reason = commandArguments[1] as String

                    ConfigManager.report.reportedPlayers.put(
                        player.uniqueId.toString(), PlayerReportData(
                            uuid = UUID.randomUUID().toString(),
                            message = reason,
                            reporterUUID = (commandSource as Player).UUID.toString(),
                            createdAt = Instant.now().toString(),
                            playerUUID = player.uniqueId.toString(),
                        )
                    )
                    ConfigManager.save()

                    commandSource.sendMessage(mm.deserialize("<color:#85ff91>Your report will now be processed.</color> <color:#ff6e88>Thank you for your support.</color>\n<gray>We may contact you if we have any questions. Join our Discord for this. \n\n(https://crystopia.link/discord)</gray>"))
                })
            }
        }
    }

}