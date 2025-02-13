package me.jesforge.servermanager;

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import me.jesforge.servermanager.config.ConfigManager
import org.bukkit.plugin.java.JavaPlugin


class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
    }

    init {
        instance = this

    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(true))

        logger.info("Loading Plugin...")

    }

    override fun onEnable() {
        CommandAPI.onEnable()

        // val settings = ConfigManager.settings

        logger.info("Plugin enabled!")

    }

    override fun onDisable() {
        CommandAPI.onDisable()

        logger.info("Plugin disabled!")
    }


}
