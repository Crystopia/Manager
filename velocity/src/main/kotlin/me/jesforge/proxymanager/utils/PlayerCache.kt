package me.jesforge.proxymanager.utils

import me.jesforge.proxymanager.config.ConfigManager

class PlayerCache {

    fun getPlayerfromCache(playeruuid: String): String {
        val player = ConfigManager.mcPlayerCache.mcPlayers.forEach { cache ->
            if (cache.uuid == playeruuid) {
                return cache.name
            } else {
                return playeruuid
            }
        }
        return playeruuid
    }
}