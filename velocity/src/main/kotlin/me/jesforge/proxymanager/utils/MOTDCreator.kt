package me.jesforge.proxymanager.utils

import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.server.ServerPing
import com.velocitypowered.api.util.Favicon
import me.jesforge.proxymanager.config.ConfigManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.util.*

fun maintenanceMotdCreator(event: ProxyPingEvent) {
    val mm = MiniMessage.miniMessage()

    val motdText = mm.deserialize(ConfigManager.settings.maintenance.motd.description)

    val fakePlayerName = LegacyComponentSerializer.legacySection().serialize(
        Component.text(ConfigManager.settings.maintenance.motd.versionText)
            .append(Component.text(ConfigManager.settings.maintenance.motd.hover))
    )

    val newPing = ServerPing.builder().version(event.ping.version).samplePlayers(
        ServerPing.SamplePlayer(
            fakePlayerName, UUID.randomUUID()
        )
    ).description(motdText)
        .favicon(Favicon("https://crystopia.net/_next/image?url=%2Fimages%2Fcrystopia.png&w=828&q=75"))
        .build()

    event.ping = newPing
}

fun defaultMotdCreator(event: ProxyPingEvent) {
    val mm = MiniMessage.miniMessage()

    val motdText = mm.deserialize(ConfigManager.settings.motd.description)

    val fakePlayerName = LegacyComponentSerializer.legacySection().serialize(
        Component.text(ConfigManager.settings.motd.versionText)
            .append(Component.text(ConfigManager.settings.motd.hover))
    )

    val newPing = ServerPing.builder().version(event.ping.version).samplePlayers(
        ServerPing.SamplePlayer(
            fakePlayerName, UUID.randomUUID()
        )
    ).description(motdText)
        .favicon(Favicon("https://crystopia.net/_next/image?url=%2Fimages%2Fcrystopia.png&w=828&q=75"))
        .build()

    event.ping = newPing
}