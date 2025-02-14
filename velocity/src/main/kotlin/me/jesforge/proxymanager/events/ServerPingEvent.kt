package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.utils.defaultMotdCreator
import me.jesforge.proxymanager.utils.maintenanceMotdCreator
import net.kyori.adventure.text.minimessage.MiniMessage

class ServerPingEvent {

    @Subscribe
    fun onServerPingEvent(event: ProxyPingEvent) {
        if (ConfigManager.settings.maintenance.maintenance == true) {
            defaultMotdCreator(event)
        } else {
            maintenanceMotdCreator(event)
        }

    }

}