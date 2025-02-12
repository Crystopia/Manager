package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult
import me.jesforge.proxymanager.utils.getLuckPermsPrefix
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

class ChatEvent {

    @Subscribe
    fun onChatEvent(event: PlayerChatEvent) {
        val player = event.player
        val message = event.message
        val mm = MiniMessage.miniMessage()

        val formattedMessage: Component = mm.deserialize("${getLuckPermsPrefix(player)}: $message")

        player.currentServer.ifPresent { currentServer ->
            currentServer.server.playersConnected.forEach { serverPlayer ->
                serverPlayer.sendMessage(formattedMessage)
            }
        }
    }
}