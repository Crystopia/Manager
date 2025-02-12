package me.jesforge.proxymanager.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.player.TabListEntry
import me.jesforge.proxymanager.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

class TabList {
    val mm = MiniMessage.miniMessage()

    @Subscribe
    fun onJoin(event: ServerPostConnectEvent) {
        val player = event.player

        updateTabList(player)

    }

    private fun updateTabList(player: Player) {
        val tabList = player.tabList

        player.sendPlayerListHeaderAndFooter(
            mm.deserialize(
                "          <gray><i>ᴘʟᴀʏɪɴɢ ᴏɴ ᴄʀʏѕᴛᴏᴘɪᴀ.ɴᴇᴛ</i></gray>          \n\n" + "\n\n" + "\n\uD83E\uDD91\uF801\uD83D\uDEF9\uF801\uD83D\uDEB2\uF801⚙\uF801\uFE0F\uF801\uD83D\uDE18\n" + "" + "\n"
            ), // Header
            mm.deserialize(
                "\n\n          <color:#c0ff73>ʏᴏᴜ ᴀʀᴇ ᴄᴜʀʀᴇɴᴛʟʏ ᴏɴ {server}</color>          \n          <gray>ѕᴇʀᴠᴇʀ: {serverOnline} <st><b> </b></st> ɴᴇᴛᴡᴏʀᴋ: {networkOnline}</gray>          \n\n<gray></gray>".replace(
                    "{server}", player.currentServer.get().server.serverInfo.name
                ).replace("{serverOnline}", player.currentServer.get().server.playersConnected.size.toString())
                    .replace("{networkOnline}", Main.instance.server.playerCount.toString())
            ) // Footer
        )

        tabList.entries.forEach { tabList.removeEntry(it.profile.id) }

        Main.instance.server.allPlayers.forEach { onlinePlayer ->
            tabList.addEntry(
                TabListEntry.builder().tabList(tabList).profile(onlinePlayer.gameProfile)
                    .displayName(getLuckPermsPrefix(onlinePlayer)).gameMode(1).build()
            )
        }
    }

    private fun getLuckPermsPrefix(player: Player): Component {
        val luckPerms = Main.instance.luckpermsAPI
        val user = luckPerms!!.userManager.getUser(player.uniqueId)

        val prefix = user?.cachedData?.metaData?.prefix
        return mm.deserialize(prefix.toString() + " " + player.username)
    }
}