package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.utils.ChatMode
import me.jesforge.proxymanager.utils.ChatNoitfiy
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

        if (playerData!!.chatMode == ChatMode.NONE || playerData!!.chatNotify == ChatNoitfiy.NONE) {
            player.sendMessage(mm.deserialize("<color:#f8ffd4>You are in mute mode and cannot see or write messages. <gray>Use /chat to change your mode.</gray></color>"))
            return@onChatEvent
        }

        if (playerData!!.chatMode == ChatMode.ALL || playerData!!.chatNotify == ChatNoitfiy.ALL) {


            Main.instance.server.allPlayers.forEach { players ->
                ConfigManager.player.players.forEach { player ->
                    if (player.value.chatNotify == ChatNoitfiy.ALL || player.value.chatMode == ChatMode.ALL) {
                        players.sendMessage(formattedMessage)
                    } else {
                    }
                }
            }
        } else if (playerData!!.chatMode == ChatMode.SERVER || playerData!!.chatNotify == ChatNoitfiy.SERVER) {

            Main.instance.server.allPlayers.forEach { players ->
                ConfigManager.player.players.forEach { player ->
                    if (player.value.chatMode == ChatMode.SERVER || player.value.chatNotify == ChatNoitfiy.SERVER) {
                        players.sendMessage(formattedMessage)
                    } else {

                    }
                }

            }
        }


    }
}