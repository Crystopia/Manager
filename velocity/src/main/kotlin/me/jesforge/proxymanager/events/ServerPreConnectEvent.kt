package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.MinecraftPlayer
import net.kyori.adventure.text.minimessage.MiniMessage
import java.time.Instant

class ServerPreConnectEvent {

    @Subscribe
    fun onServerPreConnectEvent(event: ServerPreConnectEvent) {
        val player = event.player
        val mm = MiniMessage.miniMessage()
        val maintenanceServerProfile =
            ConfigManager.settings.maintenance.maintenanceServer[event.originalServer.serverInfo.name]

        if (maintenanceServerProfile != null && maintenanceServerProfile.enabled) {
            if (player.hasPermission(maintenanceServerProfile.permission)) {
                player.sendMessage(mm.deserialize("<color:#a1e9ff>You can join the server because you are whitelisted!</color>"))
            } else {
                player.sendMessage(mm.deserialize("<color:#ff9d94>This server is currently not activated for you! Please try again later!</color>"))
                event.result = ServerPreConnectEvent.ServerResult.denied()
            }
        }
    }


}