package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.MinecraftPlayer
import me.jesforge.proxymanager.config.Player
import me.jesforge.proxymanager.utils.ChatModeType
import me.jesforge.proxymanager.utils.ChatNoitfiyType
import me.jesforge.proxymanager.utils.ParseTime
import me.jesforge.proxymanager.utils.updateTabList
import net.kyori.adventure.text.minimessage.MiniMessage
import java.time.Instant

class JoinEvent {
    private val mm = MiniMessage.miniMessage()

    @Subscribe
    fun onJoin(event: ServerPostConnectEvent) {
        val player = event.player
        val playerUUID = player.uniqueId.toString()

        ConfigManager.report.reportArchiv.forEach { (_, report) ->
            if (report.reporterUUID == playerUUID && !report.notified) {
                val resolveMSG = if (report.resolve == true) {
                    "<color:#ffc44f>Thank you for your report! It has been edited and is complete. <gray>(Archived)</gray></color>"
                } else {
                    "<color:#ffc44f>We were unable to process your report and it has been archived.</color>"
                }
                report.notified = true
                ConfigManager.save()
                player.sendMessage(mm.deserialize(resolveMSG))
            }
        }

        updateTabList(player)
    }
}