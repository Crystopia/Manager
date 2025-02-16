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
    val banArchiv: MutableList<BannedPlayer> = mutableListOf(),
    val templates: MutableMap<String, TemplatesData> = mutableMapOf(),
    var banMessage: String = "<color:#ff4133>You have been blocked and can no longer connect!</color>\n\n<color:#ff5e24>Your sentence is still <color:#fff2ab>{days}d {hours}h {minutes}m {seconds}s.</color></color>\n<color:#bababa>When your punishment is over, stick to our rules!</color>\n\n<color:#595959>Ban information</color>\n<color:#595959>Reason: <gray>{message}</gray></color>\n<color:#595959>ID: {id} UUID: {uuid} Created at: {createdAt}</color>"
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
    var createdAt: String,
    var banUUID: String,
    var banIntID: String,
    var reason: String,
    var ended: Boolean = false,
)

@Serializable
data class ReportData(
    val reportedPlayers: MutableMap<String, PlayerReportData> = mutableMapOf(),
    val reportArchiv: MutableMap<String, PlayerReportData> = mutableMapOf(),
    var reportAdminPermission: String = "crystopia.commands.report.managment",
)

@Serializable
data class PlayerReportData(
    var uuid: String,
    var createdAt: String,
    var message: String,
    var images: MutableList<String>,
    var collaborators: MutableList<String> = mutableListOf(),
    var playerDiscordUsername: String,
    var playerUUID: String,
    var reporterUUID: String,
    var claim: String? = null,
    var resolve: Boolean? = null,
    var notified: Boolean = false,
)

@Serializable
data class MinecraftPlayerData(
    var mcPlayers: MutableList<MinecraftPlayer> = mutableListOf(),
)

@Serializable
data class MinecraftPlayer(
    var uuid: String,
    var joinedAt: String,
    var lastJoinedAt: String,
    var name: String,
)

@Serializable
data class CommandSettingsData(
    val servers: MutableMap<String, CommandPerissionData> = mutableMapOf(),
    val bypassPermissions: String = "crystopia.bypass.commandblock",
    val commandErrorMessage: String = "<color:#ff4c38>This command is not available for you or could not be found.</color>"
)

@Serializable
data class CommandPerissionData(
    val permissions: MutableMap<String, Command> = mutableMapOf(),
)

@Serializable
data class Command(
    val command: MutableList<String> = mutableListOf(),
    val tabComplete: MutableList<String> = mutableListOf(),
)

