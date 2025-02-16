package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
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
import java.time.OffsetDateTime
import kotlin.math.expm1
import kotlin.time.Duration

class JoinEvent {
    val mm = MiniMessage.miniMessage()

    @Subscribe
    fun onJoin(event: ServerPostConnectEvent) {
        val mm = MiniMessage.miniMessage()
        val player = event.player

        if (ConfigManager.mcPlayerCache.mcPlayers.isEmpty()) {
            ConfigManager.mcPlayerCache.mcPlayers.add(
                MinecraftPlayer(
                    uuid = player.uniqueId.toString(),
                    name = player.username,
                    lastJoinedAt = Instant.now().toString(),
                    joinedAt = Instant.now().toString(),
                )
            )
            ConfigManager.save()
        }
        ConfigManager.mcPlayerCache.mcPlayers.forEach { mcPlayer ->
            if (mcPlayer.uuid == player.uniqueId.toString()) {
                ConfigManager.mcPlayerCache.mcPlayers.remove(mcPlayer)
                ConfigManager.mcPlayerCache.mcPlayers.add(
                    MinecraftPlayer(
                        uuid = player.uniqueId.toString(),
                        name = player.username,
                        lastJoinedAt = Instant.now().toString(),
                        joinedAt = mcPlayer.joinedAt
                    )
                )
                ConfigManager.save()
            } else {
                ConfigManager.mcPlayerCache.mcPlayers.add(
                    MinecraftPlayer(
                        uuid = player.uniqueId.toString(),
                        name = player.username,
                        lastJoinedAt = Instant.now().toString(),
                        joinedAt = Instant.now().toString(),
                    )
                )
                ConfigManager.save()
            }
        }


        if (!ConfigManager.settings.serverData.versionData.protocols.contains(event.player.protocolVersion.protocol)) {
            player.disconnect(
                mm.deserialize(
                    ConfigManager.settings.serverData.versionData.kickMessage.replace(
                        "{versions}", ConfigManager.settings.serverData.versionData.version.toString()
                    )
                )
            )
        }

        if (ConfigManager.settings.maintenance.maintenance) {
            if (player.hasPermission(ConfigManager.settings.maintenance.bypassPermission)) {
                player.sendMessage(mm.deserialize("<color:#d4daff>Bear in mind that maintenance work is currently in progress!</color>"))
            } else {
                player.disconnect(
                    mm.deserialize(
                        "<color:#ffe8a8>Hey <b><color:#e5ffc4>${player.username}</color></b>, our network is currently undergoing maintenance work. \n" + "We are working for your gaming experience. \nPlease wait until we are do</color>\n" + "\nOn our <color:#66bfff>#discord</color> <gray>(ʜᴛᴛᴘѕ://ᴄʀʏѕᴛᴏᴘɪᴀ.ʟɪɴᴋ/ᴅɪѕᴄᴏʀᴅ)</gray>  \nyou can get more information about \nthe current status of the maintenance work.\n" + "\n\n" + "\n<gray>---------------</gray>\n<color:#d8d4ff>ʀᴏʙɪᴛ ɪѕ ᴛʜɪɴᴋɪɴɢ...</color>"
                    )
                )
            }
        }

        val playerLimit = ConfigManager.settings.serverData.playerLimit

        if (playerLimit == Main.instance.server.allPlayers.size - 1) {
            val player = Main.instance.server.getPlayer(event.player.uniqueId).get()

            if (player.hasPermission(ConfigManager.settings.serverData.bypassJoinLimitPermission)) {
            } else {
                player.disconnect(mm.deserialize("<i><color:#99faff>The player limit has been reached! And you can only join again later. Please wait a bit.</color></i>"))
            }
        } else {

        }

        if (ConfigManager.player.players[player.toString()] == null) {
            ConfigManager.player.players[player.uniqueId.toString()] = Player(
                UUID = player.uniqueId.toString(),
                chatMode = ChatModeType.ALL,
                chatNotify = ChatNoitfiyType.ALL,
            )
            ConfigManager.save()
        }

        if (player.hasPermission(ConfigManager.report.reportAdminPermission)) {
            if (ConfigManager.report.reportedPlayers.isEmpty()) {

            } else {
                player.sendMessage(mm.deserialize("<b><click:run_command:'/report management list'><color:#ff9854>There are unedited reports for you!</color></click></b>"))
            }
        }

        val banned = ConfigManager.ban.bannedplayers[player.uniqueId.toString()]

        if (banned != null && !banned.ended) {
            val time = ParseTime().parseDuration(banned.createdAt)

            val bannedCreatedAt = Instant.parse(banned.createdAt)
            if (bannedCreatedAt.isBefore(Instant.now())) {
                banned.ended = true
                ConfigManager.ban.banArchiv.add(banned)
                ConfigManager.ban.bannedplayers.remove(banned.uuid)
                ConfigManager.save()
            } else {
                player.disconnect(
                    mm.deserialize(
                        ConfigManager.ban.banMessage.replace(
                            "{player}", player.username
                        ).replace("{message}", banned.reason)
                            .replace("{days}", time.toDaysPart().toString().replace("-", ""))
                            .replace("{hours}", time.toHoursPart().toString().replace("-", ""))
                            .replace("{minutes}", time.toMinutesPart().toString().replace("-", ""))
                            .replace("{seconds}", time.toSecondsPart().toString().replace("-", ""))
                            .replace("{id}", banned.banIntID.toString()).replace("{uuid}", banned.banUUID.toString())
                            .replace(
                                "{createdAt}", banned.createdAt.split("-")[1] + "-" + banned.createdAt.split("-")[0]
                            )
                    )
                )
            }

        }

        val archiveReports = ConfigManager.report.reportArchiv
        archiveReports.forEach { report ->
            if (report.value.reporterUUID == player.uniqueId.toString() && !report.value.notified) {
                var resolve = report.value.resolve
                var resolveMSG: String

                if (resolve == true) resolveMSG =
                    "<color:#ffc44f>Thank you for your report! It has been edited and is complete. <gray>(Archived)</gray></color>"
                else resolveMSG =
                    "<color:#ffc44f>We were unable to process your report and it has been archived.</color>"

                report.value.notified = true
                ConfigManager.save()

                player.sendMessage(mm.deserialize(resolveMSG))
            }
        }

        updateTabList(player)
    }
}