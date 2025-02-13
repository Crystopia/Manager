package me.jesforge.proxymanager.config

import kotlinx.serialization.Serializable
import me.jesforge.proxymanager.utils.ChatMode
import me.jesforge.proxymanager.utils.ChatNoitfiy

@Serializable
data class SettingsData(
    var none: String = ""
)

@Serializable
data class PlayerData(
    val players: MutableMap<String, Player> = mutableMapOf()
)

@Serializable
data class Player(
    var UUID: String,
    var chatMode: ChatMode,
    var chatNotify: ChatNoitfiy,
)
