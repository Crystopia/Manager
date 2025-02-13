package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.Player
import me.jesforge.proxymanager.utils.ChatMode
import me.jesforge.proxymanager.utils.ChatNoitfiy
import me.jesforge.proxymanager.utils.updateTabList
import net.kyori.adventure.text.minimessage.MiniMessage

class JoinEvent {
    val mm = MiniMessage.miniMessage()

    @Subscribe
    fun onJoin(event: ServerPostConnectEvent) {
        val player = event.player

        if (ConfigManager.player.players[player.toString()] == null) {
            ConfigManager.player.players[player.uniqueId.toString()] = Player(
                UUID = player.uniqueId.toString(),
                chatMode = ChatMode.SERVER,
                chatNotify = ChatNoitfiy.ALL,
            )
        }

        updateTabList(player)
    }


}