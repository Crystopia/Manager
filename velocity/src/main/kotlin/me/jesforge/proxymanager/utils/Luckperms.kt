package me.jesforge.proxymanager.utils

import com.velocitypowered.api.proxy.Player
import me.jesforge.proxymanager.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import java.util.*


fun getLuckPermsPrefix(player: Player): String {
    val mm = MiniMessage.miniMessage()
    val luckPerms = Main.instance.luckpermsAPI
    val user = luckPerms!!.userManager.getUser(player.uniqueId)

    val prefix = user?.cachedData?.metaData?.prefix
    return prefix.toString()
}

fun getGroupWeight(groupName: String): Int? {
    val luckPerms = LuckPermsProvider.get()
    val group: Group? = luckPerms.groupManager.getGroup(groupName)

    return group?.weight!!.orElse(0)
}

fun getPlayerGroupWeight(uuid: UUID): Int {
    val luckPerms = LuckPermsProvider.get()
    val user: User = luckPerms.userManager.getUser(uuid) ?: return 0

    val primaryGroup = user.getPrimaryGroup()
    return getGroupWeight(primaryGroup)!!
}