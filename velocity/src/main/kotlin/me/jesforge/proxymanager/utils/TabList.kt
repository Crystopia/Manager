package me.jesforge.proxymanager.utils

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.player.TabListEntry
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.MiniMessage

fun updateTabList(player: Player) {
    val tabList = player.tabList
    val mm = MiniMessage.miniMessage()

    player.sendPlayerListHeaderAndFooter(
        mm.deserialize(
            "<gray><i>ᴘʟᴀʏɪɴɢ ᴏɴ ᴄʀʏѕᴛᴏᴘɪᴀ.ɴᴇᴛ</i></gray>\n\n\n\n\n\n\uD83E\uDD91\uF801\uD83D\uDEF9\uF801\uD83D\uDEB2\uF801⚙\uF801\uFE0F\uF801\uD83D\uDE18\n"
        ), // Header
        mm.deserialize(
            "\n\n          <color:#c0ff73>ʏᴏᴜ ᴀʀᴇ ᴄᴜʀʀᴇɴᴛʟʏ ᴏɴ ${player.currentServer.get().server.serverInfo.name}</color>          \n          <gray>ѕᴇʀᴠᴇʀ: ${player.currentServer.get().server.playersConnected.size} <st><b> </b></st> ɴᴇᴛᴡᴏʀᴋ: ${Main.instance.server.playerCount}</gray>          \n\nᴄʜᴀᴛᴍᴏᴅᴇ: {chatmode}                                           \nᴠᴇʀѕɪᴏɴ: {version}                                             \n<gray></gray>"
                .replace("{version}", ConfigManager.settings.serverData.serverVersion)
                .replace("{chatmode}", ConfigManager.player.players[player.uniqueId.toString()]!!.chatMode.toString())
        )
    )

    tabList.entries.forEach { tabList.removeEntry(it.profile.id) }

    Main.instance.server.allPlayers.forEach { onlinePlayer ->
        val onlineTabList = onlinePlayer.tabList

        onlineTabList.entries.forEach { onlineTabList.removeEntry(it.profile.id) }

        Main.instance.server.allPlayers.forEach { targetPlayer ->
            onlineTabList.addEntry(
                TabListEntry.builder().tabList(onlineTabList).profile(targetPlayer.gameProfile)
                    .displayName(mm.deserialize("${getLuckPermsPrefix(targetPlayer)} ${targetPlayer.username}"))
                    .gameMode(1)
                    .listed(true)
                    .build()
            )
        }
    }

}