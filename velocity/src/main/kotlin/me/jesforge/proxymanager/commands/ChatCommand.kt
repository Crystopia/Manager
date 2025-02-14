package me.jesforge.proxymanager.commands

import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.utils.ChatModeType
import me.jesforge.proxymanager.utils.ChatNoitfiyType
import me.jesforge.proxymanager.utils.updateTabList
import net.kyori.adventure.text.minimessage.MiniMessage

class ChatCommand {
    val mm = MiniMessage.miniMessage()
    val command = commandTree("chat") {
        literalArgument("chatmode") {
            literalArgument("server") {
                executes(CommandExecutor { commandSource, commandArguments ->
                    val player = ConfigManager.player.players[(commandSource as Player).uniqueId.toString()]

                    if (player!!.chatNotify == ChatNoitfiyType.SERVER) {
                        commandSource.sendMessage(mm.deserialize("<color:#ff91a5>The server mode is active and you will only receive messages from the server.</color>"))
                    } else {
                        player.chatNotify = ChatNoitfiyType.SERVER
                        player.chatMode = ChatModeType.SERVER
                        ConfigManager.save()

                        updateTabList(commandSource)
                        commandSource.sendMessage(mm.deserialize("<color:#f8ffd4>You have activated server mode and now only receive server messages.</color>"))
                    }
                })
            }
            literalArgument("all") {
                executes(CommandExecutor { commandSource, commandArguments ->
                    val player = ConfigManager.player.players[(commandSource as Player).uniqueId.toString()]

                    if (player!!.chatNotify == ChatNoitfiyType.ALL) {
                        commandSource.sendMessage(mm.deserialize("<color:#ff91a5>The global mode is active and you will receive messages from the server and the network.</color>"))
                    } else {
                        player.chatNotify = ChatNoitfiyType.ALL
                        player.chatMode = ChatModeType.ALL
                        ConfigManager.save()

                        updateTabList(commandSource)
                        commandSource.sendMessage(mm.deserialize("<color:#f8ffd4>You have activated the global mode and now only receive global messages.</color>"))
                    }

                })
            }
            literalArgument("none") {
                executes(CommandExecutor { commandSource, commandArguments ->
                    val player = ConfigManager.player.players[(commandSource as Player).uniqueId.toString()]

                    if (player!!.chatNotify == ChatNoitfiyType.NONE) {
                        commandSource.sendMessage(mm.deserialize("<color:#ff91a5>You already don't get any messages</color>"))
                    } else {
                        player.chatNotify = ChatNoitfiyType.NONE
                        player.chatMode = ChatModeType.NONE
                        ConfigManager.save()

                        updateTabList(commandSource)
                        commandSource.sendMessage(mm.deserialize("<color:#f8ffd4>You now get no messages from players!</color>"))
                    }
                })
            }

        }
    }


}