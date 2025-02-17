package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.MinecraftPlayer
import me.jesforge.proxymanager.config.Player
import me.jesforge.proxymanager.utils.ChatModeType
import me.jesforge.proxymanager.utils.ChatNoitfiyType
import me.jesforge.proxymanager.utils.ParseTime
import net.kyori.adventure.text.minimessage.MiniMessage
import java.time.Instant

class ServerPreConnectEvent {

    @Subscribe
    fun onServerPreConnectEvent(event: ServerPreConnectEvent) {
        val player = event.player
        val playerUUID = player.uniqueId.toString()
        val mm = MiniMessage.miniMessage()
        val maintenanceServerProfile =
            ConfigManager.settings.maintenance.maintenanceServer[event.originalServer.serverInfo.name]

        val existingPlayer = ConfigManager.mcPlayerCache.mcPlayers.find { it.uuid == playerUUID }
        if (existingPlayer != null) {
            existingPlayer.lastJoinedAt = Instant.now().toString()
        } else {
            ConfigManager.mcPlayerCache.mcPlayers.add(
                MinecraftPlayer(
                    uuid = playerUUID,
                    name = player.username,
                    lastJoinedAt = Instant.now().toString(),
                    joinedAt = Instant.now().toString()
                )
            )
        }
        ConfigManager.save()

        if (!ConfigManager.settings.serverData.versionData.protocols.contains(player.protocolVersion.protocol)) {
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
                        "<color:#ffe8a8>Hey <b><color:#e5ffc4>${player.username}</color></b>, our network is currently undergoing maintenance work. \n" + "We are working for your gaming experience. \nPlease wait until we are done.</color>\n" + "\nOn our <color:#66bfff>#discord</color> <gray>(ʜᴛᴛᴘѕ://ᴄʀʏѕᴛᴏᴘɪᴀ.ʟɪɴᴋ/ᴅɪѕᴄᴏʀᴅ)</gray>  \nyou can get more information about \nthe current status of the maintenance work.\n" + "\n\n" + "\n<gray>---------------</gray>\n<color:#d8d4ff>ʀᴏʙɪᴛ ɪѕ ᴛʜɪɴᴋɪɴɢ...</color>"
                    )
                )
            }
        }

        val playerLimit = ConfigManager.settings.serverData.playerLimit
        if (Main.instance.server.allPlayers.size >= playerLimit) {
            if (!player.hasPermission(ConfigManager.settings.serverData.bypassJoinLimitPermission)) {
                player.disconnect(mm.deserialize("<i><color:#99faff>The player limit has been reached! And you can only join again later. Please wait a bit.</color></i>"))
            }
        }

        if (ConfigManager.player.players[playerUUID] == null) {
            ConfigManager.player.players[playerUUID] = Player(
                UUID = playerUUID, chatMode = ChatModeType.ALL, chatNotify = ChatNoitfiyType.ALL
            )
            ConfigManager.save()
        }

        if (player.hasPermission(ConfigManager.report.reportAdminPermission) && ConfigManager.report.reportedPlayers.isNotEmpty()) {
            player.sendMessage(mm.deserialize("<b><click:run_command:'/report management list'><color:#ff9854>There are unedited reports for you!</color></click></b>"))
        }

        val banned = ConfigManager.ban.bannedplayers[playerUUID]
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
                        ConfigManager.ban.banMessage.replace("{player}", player.username)
                            .replace("{message}", banned.reason)
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