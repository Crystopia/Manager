package me.jesforge.proxymanager.tasks

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.scheduler.ScheduledTask
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.utils.updateTabList
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

object TabListScheduler {

    private var task: ScheduledTask? = null

    fun start(server: ProxyServer) {
        task = server.scheduler.buildTask(Main.instance, Consumer {
            Main.instance.server.allPlayers.forEach { player ->
                updateTabList(player)
            }
        }).repeat(10, TimeUnit.SECONDS).schedule()
    }

    fun stop() {
        task?.cancel()
    }
}
