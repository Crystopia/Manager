package me.jesforge.proxymanager.events

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.BannedPlayer
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.utils.ParseTime
import me.jesforge.proxymanager.utils.TypeVariabeln
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.time.Instant
import java.util.*

class NetworkMessaging {

    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        println(event)
        if (!event.getIdentifier().getId().equals("networkmanager:channel")) return;

        val input: ByteArrayDataInput = ByteStreams.newDataInput(event.getData());
        val subChannel = input.readUTF()

        if (subChannel.equals("kick")) {
            val targetName = input.readUTF()
            val reason = input.readUTF()

            Main.instance.server.getPlayer(targetName).get().disconnect(Component.text(reason));
        }
        if (subChannel.equals("ban")) {
            val targetName = input.readUTF()
            val time = ParseTime().parseTimeString(input.readUTF() as String)
            val message = input.readUTF()


            val isPlayer = Main.instance.server.getPlayer(targetName as String)

            if (isPlayer.isPresent) {
                val player = Main.instance.server.getPlayer(targetName as String).get()


                val banned = ConfigManager.ban.bannedplayers[player.uniqueId.toString()]
                if (banned != null && !banned.ended) {
                    return
                }
                val timestamp = Instant.now().plus(time).toString()

                ConfigManager.ban.bannedplayers[player.uniqueId.toString()] = BannedPlayer(
                    uuid = player.uniqueId.toString(),
                    banUUID = UUID.randomUUID().toString(),
                    createdAt = timestamp,
                    reason = message,
                    banIntID = TypeVariabeln().getBanIntID(),
                )
                ConfigManager.save()


                val ban = ConfigManager.ban.bannedplayers[player.uniqueId.toString()]

                val parts = ban!!.createdAt.split("-")
                val formattedDate = "${parts[1]}-${parts[0]}"
                player.disconnect(
                    MiniMessage.miniMessage().deserialize(
                        ConfigManager.ban.banMessage.replace(
                            "{player}", player.username
                        ).replace("{message}", message)
                            .replace("{days}", time.toDaysPart().toString().replace("-", ""))
                            .replace("{hours}", time.toHoursPart().toString().replace("-", ""))
                            .replace("{minutes}", time.toMinutesPart().toString().replace("-", ""))
                            .replace("{seconds}", time.toSecondsPart().toString().replace("-", ""))
                            .replace("{id}", ban!!.banIntID.toString())
                            .replace("{uuid}", ban.banUUID.toString())
                            .replace("{createdAt}", formattedDate)
                    )
                )
            } else {
                val uuid =
                    ConfigManager.mcPlayerCache.mcPlayers.find { player -> player.name == targetName }?.uuid.toString()


                val banned = ConfigManager.ban.bannedplayers[uuid]
                if (banned != null && !banned.ended) {
                    return
                }
                val timestamp = Instant.now().plus(time).toString()

                ConfigManager.ban.bannedplayers[uuid] = BannedPlayer(
                    uuid = uuid,
                    banUUID = UUID.randomUUID().toString(),
                    createdAt = timestamp,
                    reason = message,
                    banIntID = TypeVariabeln().getBanIntID(),
                )
                ConfigManager.save()

            }


        }
    }
}