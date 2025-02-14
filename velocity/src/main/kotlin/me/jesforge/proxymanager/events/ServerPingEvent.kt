package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.server.ServerInfo
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.utils.defaultMotdCreator
import me.jesforge.proxymanager.utils.maintenanceMotdCreator
import me.jesforge.proxymanager.utils.versionMotdCreator
import net.kyori.adventure.text.minimessage.MiniMessage

class ServerPingEvent {

    @Subscribe
    fun onServerPingEvent(event: ProxyPingEvent) {
        val protocols = ConfigManager.settings.serverData.versionData.protocols
        val connection = event.connection

        if (!protocols.contains(connection.protocolVersion.protocol)) {
            versionMotdCreator(event)
        } else if (ConfigManager.settings.maintenance.maintenance == true) {
            maintenanceMotdCreator(event)
        } else {
            defaultMotdCreator(event)
        }
    }
}