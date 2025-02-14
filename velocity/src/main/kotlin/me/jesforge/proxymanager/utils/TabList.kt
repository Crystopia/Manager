package me.jesforge.proxymanager.utils

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.player.TabListEntry
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import net.kyori.adventure.text.minimessage.MiniMessage

fun updateTabList(player: Player) {
    val tabList = player.tabList
    val mm = MiniMessage.miniMessage()

    player.sendPlayerListHeaderAndFooter(
        mm.deserialize(
            "          <gray><i>ᴘʟᴀʏɪɴɢ ᴏɴ ᴄʀʏѕᴛᴏᴘɪᴀ.ɴᴇᴛ</i></gray>          \n\n" + "\n\n" + "\n\uD83E\uDD91\uF801\uD83D\uDEF9\uF801\uD83D\uDEB2\uF801⚙\uF801\uFE0F\uF801\uD83D\uDE18\n" + "" + "\n"
        ), // Header
        mm.deserialize(
            "\n\n          <color:#c0ff73>ʏᴏᴜ ᴀʀᴇ ᴄᴜʀʀᴇɴᴛʟʏ ᴏɴ {server}</color>          \n" + "\n          <gray>ѕᴇʀᴠᴇʀ: {serverOnline} <st><b> </b></st> ɴᴇᴛᴡᴏʀᴋ: {networkOnline}</gray>          \n" + "\n" + "ᴄʜᴀᴛᴍᴏᴅᴇ: {chatmode}                                                         \nᴠᴇʀѕɪᴏɴ: {version}                                                              \n<gray></gray>".replace(
                "{server}", player.currentServer.get().server.serverInfo.name
            ).replace("{serverOnline}", player.currentServer.get().server.playersConnected.size.toString())
                .replace("{networkOnline}", Main.instance.server.playerCount.toString())
                .replace("{version}", ConfigManager.settings.serverData.version)
                .replace("{chatmode}", ConfigManager.player.players[player.uniqueId.toString()]!!.chatMode.toString())
        ) // Footer
    )

    tabList.entries.forEach { tabList.removeEntry(it.profile.id) }

    Main.instance.server.allPlayers.forEach { onlinePlayer ->
        tabList.addEntry(
            TabListEntry.builder().tabList(tabList).profile(onlinePlayer.gameProfile)
                .displayName(mm.deserialize(getLuckPermsPrefix(onlinePlayer))).gameMode(1).build()
        )
    }
}