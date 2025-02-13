package me.jesforge.servermanager.events

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatEvent : Listener {

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true
    }

}