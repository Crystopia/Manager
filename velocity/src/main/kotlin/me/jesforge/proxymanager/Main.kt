package me.jesforge.proxymanager;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIVelocityConfig
import me.jesforge.proxymanager.commands.ChatCommand
import me.jesforge.proxymanager.commands.MaintenanceCommand
import me.jesforge.proxymanager.commands.NetworkCommand
import me.jesforge.proxymanager.commands.ReportCommand
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.events.*
import me.jesforge.proxymanager.tasks.TabListScheduler
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.slf4j.Logger


class Main @Inject constructor(val logger: Logger, val server: ProxyServer) {

    companion object {
        lateinit var instance: Main
    }

    val CHANNEL: MinecraftChannelIdentifier = MinecraftChannelIdentifier.create("networkmanager", "channel")
    var luckpermsAPI: LuckPerms? = null

    init {
        instance = this

    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        CommandAPI.onLoad(CommandAPIVelocityConfig(server, this))
        CommandAPI.onEnable();

        logger.info("Loaded proxy manager")

        val settings = ConfigManager.settings
        val player = ConfigManager.player
        val bans = ConfigManager.ban
        val reports = ConfigManager.report
        val mcPlayerCache = ConfigManager.mcPlayerCache
        val commands = ConfigManager.commands

        MaintenanceCommand()
        ChatCommand()
        NetworkCommand()
        ReportCommand()

        this.luckpermsAPI = LuckPermsProvider.get()

        server.channelRegistrar.register(CHANNEL);

        server.eventManager.register(this, NetworkMessaging())
        server.eventManager.register(this, ServerPreConnectEvent())
        server.eventManager.register(this, ServerPingEvent())
        server.eventManager.register(this, JoinEvent())
        server.eventManager.register(this, ChatEvent())
        server.eventManager.register(this, CommandManagerEvent())

        TabListScheduler.start(server)
    }


    fun loadMaintenanceData() {
        if (ConfigManager.settings.maintenance.maintenance) {

        }
    }
}
