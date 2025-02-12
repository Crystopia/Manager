package me.jesforge.proxymanager;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import me.jesforge.proxymanager.events.ChatEvent
import me.jesforge.proxymanager.events.TabList
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.slf4j.Logger

@Plugin(
    id = "proxymanager",
    name = "ProxyManager",
    version = "1.0.0",
    authors = ["jesforge"],
    dependencies = [Dependency(id = "luckperms")]
)
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

        this.luckpermsAPI = LuckPermsProvider.get()

        server.eventManager.register(this, TabList())
        server.eventManager.register(this, ChatEvent())
    }
}
