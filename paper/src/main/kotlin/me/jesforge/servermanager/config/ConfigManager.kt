package me.jesforge.servermanager.config

import kotlinx.serialization.encodeToString
import java.io.File

object ConfigManager {

    private val settingsFile = File("plugins/ServerManager/config.json")

    val settings = settingsFile.loadConfig(SettingsData)

    fun save() {
        settingsFile.writeText(json.encodeToString(settings))
    }

}