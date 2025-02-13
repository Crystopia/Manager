package me.jesforge.proxymanager.config

import java.io.File

object ConfigManager {

    private val settingsFile = File("plugins/ProxyManager/config.json")
    private val playerFile = File("plugins/ProxyManager/player.json")

    val settings = settingsFile.loadConfig(SettingsData())
    val player = playerFile.loadConfig(PlayerData())

    fun save() {
        settingsFile.writeText(json.encodeToString(settings))
    }

}