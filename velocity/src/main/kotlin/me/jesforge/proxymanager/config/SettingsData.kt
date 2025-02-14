package me.jesforge.proxymanager.config

import kotlinx.serialization.Serializable
import me.jesforge.proxymanager.utils.ChatModeType
import me.jesforge.proxymanager.utils.ChatNoitfiyType

@Serializable
data class SettingsData(
    var maintenance: MaintenanceData,
    val bypassJoinLimitPermission: String = "",
    var motd: ServerMotdData,
    var serverData: ServerData,
)

@Serializable
data class ServerData(
    var version: String
)

@Serializable
data class MaintenanceData(
    var maintenance: Boolean,
    var bypassPermission: String,
    var motd: MaintenanceMotdData,
    var maintenanceServer: MutableList<String>
)

@Serializable
data class MaintenanceMotdData(
    var description: String, var hover: String, var versionText: String
)

@Serializable
data class ServerMotdData(
    var description: String, var hover: String, var versionText: String
)

@Serializable
data class PlayerData(
    val players: MutableMap<String, Player> = mutableMapOf()
)

@Serializable
data class Player(
    var UUID: String,
    var chatMode: ChatModeType,
    var chatNotify: ChatNoitfiyType,
)
