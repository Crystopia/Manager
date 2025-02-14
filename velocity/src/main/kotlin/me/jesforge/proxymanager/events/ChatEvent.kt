package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.utils.ChatModeType
import me.jesforge.proxymanager.utils.ChatNoitfiyType
import me.jesforge.proxymanager.utils.getLuckPermsPrefix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

class ChatEvent {

    @Subscribe
    fun onChatEvent(event: PlayerChatEvent) {
        val mm = MiniMessage.miniMessage()
        val player = event.player
        val playerData = ConfigManager.player.players[player.uniqueId.toString()]
        val message = event.message
        val formattedMessage: Component = mm.deserialize("${getLuckPermsPrefix(player)}: $message")

        if (playerData!!.chatMode == ChatModeType.NONE || playerData!!.chatNotify == ChatNoitfiyType.NONE) {
            player.sendMessage(mm.deserialize("<color:#f8ffd4>You are in mute mode and cannot see or write messages. <gray>Use /chat to change your mode.</gray></color>"))
            return@onChatEvent
        }

        if (playerData!!.chatMode == ChatModeType.ALL || playerData!!.chatNotify == ChatNoitfiyType.ALL) {


            Main.instance.server.allPlayers.forEach { players ->
                ConfigManager.player.players.forEach { player ->
                    if (player.value.chatNotify == ChatNoitfiyType.ALL || player.value.chatMode == ChatModeType.ALL) {
                        players.sendMessage(formattedMessage)
                    } else {
                    }
                }
            }
        } else if (playerData!!.chatMode == ChatModeType.SERVER || playerData!!.chatNotify == ChatNoitfiyType.SERVER) {

            Main.instance.server.allPlayers.forEach { players ->
                ConfigManager.player.players.forEach { player ->
                    if (player.value.chatMode == ChatModeType.SERVER || player.value.chatNotify == ChatNoitfiyType.SERVER) {
                        players.sendMessage(formattedMessage)
                    } else {

                    }
                }

            }
        }


    }
}