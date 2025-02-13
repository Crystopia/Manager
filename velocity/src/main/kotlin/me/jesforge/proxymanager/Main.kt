package me.jesforge.proxymanager;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer
import me.jesforge.proxymanager.events.ChatEvent
import me.jesforge.proxymanager.events.JoinEvent
import me.jesforge.proxymanager.config.ConfigManager
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.slf4j.Logger

class Main @Inject constructor(val logger: Logger, val server: ProxyServer) {

    companion object {
        lateinit var instance: Main
    }

    var luckpermsAPI: LuckPerms? = null

    init {
        instance = this

    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        logger.info("Loaded proxy manager")

        val settings = ConfigManager.settings
        val player = ConfigManager.player

        this.luckpermsAPI = LuckPermsProvider.get()

        server.eventManager.register(this, JoinEvent())
        server.eventManager.register(this, ChatEvent())
    }
}
