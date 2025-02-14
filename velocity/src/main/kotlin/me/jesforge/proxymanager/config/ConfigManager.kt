package me.jesforge.proxymanager.config

import java.io.File

object ConfigManager {

    private val settingsFile = File("plugins/ProxyManager/config.json")
    private val playerFile = File("plugins/ProxyManager/player.json")

    val settings = settingsFile.loadConfig(
        SettingsData(
            motd = ServerMotdData(
                description = "",
            ), serverData = ServerData(
                bypassJoinLimitPermission = "", playerLimit = 100, versionData = ServerVersionData(
                    version = mutableListOf(),
                    protocols = mutableListOf(),
                    kickMessage = "<color:#bae1ff>The current client version is no longer supported. \n" + "Please update to one of the Versions (<color:#ffeea3>{versions} </color>)\n" + "or always use the latest version \n" + "to avoid problems with some features.</color>\n" + "<gray>---------------</gray>\n" + "<color:#d8d4ff>ʀᴏʙɪᴛ ɪѕ ᴛʜɪɴᴋɪɴɢ...</color>",
                    motd = ServerVersionMotdData(
                        hoverText = "",
                        description = "",
                        versionText = "",
                    )
                ), defaultServer = "Lobby"
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