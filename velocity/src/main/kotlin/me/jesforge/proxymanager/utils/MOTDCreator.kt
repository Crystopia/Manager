package me.jesforge.proxymanager.utils

import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.server.ServerPing
import com.velocitypowered.api.util.Favicon
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.util.*

fun versionMotdCreator(event: ProxyPingEvent) {
    val mm = MiniMessage.miniMessage()

    val version = ConfigManager.settings.serverData.versionData.version
    val motdData = ConfigManager.settings.serverData.versionData.motd

    val motdText = mm.deserialize(motdData.description.replace("{version}", version.toString()))

    val hover = motdData.hoverText.replace("{version}", version.toString())

    val motdVersion = motdData.versionText.replace("{version}", version.toString())

    val newPing = ServerPing.builder().version(
        ServerPing.Version(0, motdVersion)
    ).samplePlayers(
        ServerPing.SamplePlayer(
            hover, UUID.randomUUID()
        )
    ).description(motdText).favicon(Favicon(TypeVariabeln().crystopiaICON)).build()

    event.ping = newPing
}

fun maintenanceMotdCreator(event: ProxyPingEvent) {
    val mm = MiniMessage.miniMessage()

    val motdText = mm.deserialize(ConfigManager.settings.maintenance.motd.description)

    val hover = ConfigManager.settings.maintenance.motd.hover

    val motdVersion = ConfigManager.settings.maintenance.motd.versionText

    val newPing = ServerPing.builder().version(
        ServerPing.Version(0, motdVersion)
    ).samplePlayers(
        ServerPing.SamplePlayer(
            hover, UUID.randomUUID()
        )
    ).description(motdText).favicon(Favicon(TypeVariabeln().crystopiaICON)).build()

    event.ping = newPing
}

fun defaultMotdCreator(event: ProxyPingEvent) {
    val mm = MiniMessage.miniMessage()

    val motdText = mm.deserialize(ConfigManager.settings.motd.description)

    val newPing = ServerPing.builder().version(event.ping.version).onlinePlayers(Main.instance.server.allPlayers.size)
        .maximumPlayers(ConfigManager.settings.serverData.playerLimit).description(motdText)
        .favicon(Favicon(TypeVariabeln().crystopiaICON)).build()

    event.ping = newPing
}