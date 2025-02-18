package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.proxy.Player
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

        if (playerData == null) {
            return
        }

        val formattedMessage: Component = mm.deserialize("${getLuckPermsPrefix(player)} ${player.username}: $message")

        if (playerData.chatMode == ChatModeType.NONE || playerData.chatNotify == ChatNoitfiyType.NONE) {
            player.sendMessage(mm.deserialize("<color:#f8ffd4>You are in mute mode and cannot see or write messages. <gray>Use /chat to change your mode.</gray></color>"))
            return
        }

        val messageRecipients = when {
            playerData.chatMode == ChatModeType.ALL || playerData.chatNotify == ChatNoitfiyType.ALL -> Main.instance.server.allPlayers
            playerData.chatMode == ChatModeType.SERVER || playerData.chatNotify == ChatNoitfiyType.SERVER -> player.currentServer.get().server.playersConnected
            else -> return
        }

        messageRecipients.forEach { recipient ->
            if (shouldSendToPlayer(recipient)) {
                recipient.sendMessage(formattedMessage)
            }
        }
    }

    private fun shouldSendToPlayer(player: Player): Boolean {
        val playerData = ConfigManager.player.players[player.uniqueId.toString()]
        return playerData != null && (playerData.chatMode == ChatModeType.ALL || playerData.chatNotify == ChatNoitfiyType.ALL || playerData.chatMode == ChatModeType.SERVER || playerData.chatNotify == ChatNoitfiyType.SERVER)
    }
}
