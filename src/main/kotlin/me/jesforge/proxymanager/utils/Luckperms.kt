package me.jesforge.proxymanager.utils

import com.velocitypowered.api.proxy.Player
import me.jesforge.proxymanager.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun getLuckPermsPrefix(player: Player): String {
    val mm = MiniMessage.miniMessage()
    val luckPerms = Main.instance.luckpermsAPI
    val user = luckPerms!!.userManager.getUser(player.uniqueId)

    val prefix = user?.cachedData?.metaData?.prefix
    return prefix.toString() + " " + player.username
}