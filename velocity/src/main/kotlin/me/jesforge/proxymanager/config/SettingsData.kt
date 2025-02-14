package me.jesforge.proxymanager.config

import kotlinx.serialization.Serializable
import me.jesforge.proxymanager.utils.ChatModeType
import me.jesforge.proxymanager.utils.ChatNoitfiyType

@Serializable
data class SettingsData(
    var maintenance: MaintenanceData,
    var motd: ServerMotdData,
    var serverData: ServerData,
)

@Serializable
data class ServerData(
    var version: String,
    var defaultServer: String,
    val bypassJoinLimitPermission: String = "",
    var playerLimit: Int = 100
)

@Serializable
data class ServerVersionData(
    var version: MutableList<String>,
    var protocolState: MutableList<Int>,
    val kickMessage: String,
    val motd: ServerVersionMotdData,
)

@Serializable
data class ServerVersionMotdData(
    var description: String,
    var versionText: String,
    var hoverText: String,
)

@Serializable
data class ServerMotdData(
    var description: String, var hover: String, var versionText: String
)

@Serializable
data class MaintenanceData(
    var maintenance: Boolean,
    var bypassPermission: String,
    var motd: MaintenanceMotdData,
    var maintenanceServer: MutableMap<String, MaintenanceServerData> = mutableMapOf(),
)

@Serializable
data class MaintenanceServerData(
    var permission: String,
    var enabled: Boolean,
    var name: String,
)

@Serializable
data class MaintenanceMotdData(
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
