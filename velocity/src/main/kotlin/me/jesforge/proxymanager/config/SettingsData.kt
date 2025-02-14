package me.jesforge.proxymanager.config

import com.mojang.brigadier.Message
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template
import kotlinx.serialization.Serializable
import me.jesforge.proxymanager.utils.ChatModeType
import me.jesforge.proxymanager.utils.ChatNoitfiyType
import java.sql.Timestamp
import java.time.Duration
import java.util.Date

@Serializable
data class SettingsData(
    var maintenance: MaintenanceData,
    var motd: ServerMotdData,
    var serverData: ServerData,
)

@Serializable
data class ServerData(
    var versionData: ServerVersionData,
    var serverVersion: String = "",
    var defaultServer: String,
    val bypassJoinLimitPermission: String = "",
    var playerLimit: Int = 100
)

@Serializable
data class ServerVersionData(
    var version: MutableList<String>,
    var protocols: MutableList<Int>,
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
    var description: String,
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

@Serializable
data class BanData(
    val bannedplayers: MutableMap<String, BannedPlayer> = mutableMapOf(),
    val templates: MutableMap<String, TemplatesData> = mutableMapOf(),
    var banMessage: String = "<color:#ffa069>You have been banned from the server!</color>\n" +
            "\n<color:#f7ffa1>And can only play on Crystopia.net \nagain after your punishment <gray>({time})</gray> has expired.</color>\n" +
            "\n\n" +
            "\n<color:#5effd1>Ban information:</color>\n" +
            "\n<st><gray>-</gray></st> <color:#a6f2ff><b>Reason:</b></color> <color:#fee3ff>{reason}</color>\n" +
            "\n<st><gray>-</gray></st> <color:#a6f2ff><b>Time:</b></color> <color:#fee3ff>{time}</color>\n" +
            "\n<st><gray>-</gray></st> <color:#a6f2ff><b>ID:</b></color> <color:#fee3ff>{id}</color>\n" +
            "\n<st><gray>-</gray></st> <color:#a6f2ff><b>UUID:</b></color> <color:#fee3ff>{uuid}</color>\n" +
            "\n<st><gray>-</gray></st> <color:#a6f2ff><b>Created at:</b></color> <color:#fee3ff>{created}</color>"
)

@Serializable
data class TemplatesData(
    val name: String,
    val UUID: String,
    val description: String,
    val time: String,
    val templateReason: String,
)

@Serializable
data class BannedPlayer(
    var uuid: String,
    var createdAt: String = Date().toString(),
    var banUUID: String,
    var banIntID: Int,
    var reason: String,
)

@Serializable
data class ReportData(
    val reportedPlayers: MutableMap<String, PlayerReportData> = mutableMapOf(),
)

@Serializable
data class PlayerReportData(
    var uuid: String,
    var createdAt: String = Date().toString(),
    var message: String,
    var playerUUID: String,
    var reporterUUID: String,
)