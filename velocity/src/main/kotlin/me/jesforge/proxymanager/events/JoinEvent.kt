package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.Player
import me.jesforge.proxymanager.utils.ChatModeType
import me.jesforge.proxymanager.utils.ChatNoitfiyType
import me.jesforge.proxymanager.utils.updateTabList
import net.kyori.adventure.text.minimessage.MiniMessage

class JoinEvent {
    val mm = MiniMessage.miniMessage()

    @Subscribe
    fun onJoin(event: ServerPostConnectEvent) {
        val mm = MiniMessage.miniMessage()
        val player = event.player

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
        updateTabList(player)
    }
}