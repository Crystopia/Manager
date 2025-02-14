package me.jesforge.proxymanager.config

import java.io.File

object ConfigManager {

    private val settingsFile = File("plugins/ProxyManager/config.json")
    private val playerFile = File("plugins/ProxyManager/player.json")

    val settings = settingsFile.loadConfig(
        SettingsData(
            motd = ServerMotdData(
                description = "", versionText = "", hover = ""
            ), serverData = ServerData(
                version = "1.0.0", defaultServer = "Lobby"
            ), maintenance = MaintenanceData(
                motd = MaintenanceMotdData(
                    hover = "", versionText = "", description = ""
                ),
                maintenance = false,
                bypassPermission = "",
            )

        )
    )
    val player = playerFile.loadConfig(PlayerData())

    fun save() {
        settingsFile.writeText(json.encodeToString(settings))
        playerFile.writeText(json.encodeToString(player))
    }

}