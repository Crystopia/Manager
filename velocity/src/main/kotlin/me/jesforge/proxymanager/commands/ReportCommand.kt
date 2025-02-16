package me.jesforge.proxymanager.commands

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.*
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.Player
import me.jesforge.proxymanager.config.PlayerReportData
import me.jesforge.proxymanager.utils.ParseTime
import net.kyori.adventure.text.minimessage.MiniMessage
import java.time.Instant
import java.util.UUID

class ReportCommand {
    val mm = MiniMessage.miniMessage()
    private val reportSessions = HashMap<UUID, UUID>()

    val command = commandTree("report") {
        stringArgument("player") {
            replaceSuggestions(
                ArgumentSuggestions.strings {
                    Main.instance.server.allPlayers.map { it.username }.toTypedArray()
                })
            executes(CommandExecutor { commandSource, commandArguments ->
                val player = Main.instance.server.getPlayer(commandArguments[0] as String).get()
                val uuid = UUID.randomUUID().toString()

                ConfigManager.report.reportedPlayers[uuid] = PlayerReportData(
                    uuid = uuid,
                    message = "",
                    reporterUUID = (commandSource as com.velocitypowered.api.proxy.Player).uniqueId.toString(),
                    createdAt = Instant.now().toString(),
                    playerUUID = player.uniqueId.toString(),
                    playerDiscordUsername = "",
                    images = mutableListOf(),
                    collaborators = mutableListOf(),
                )
                ConfigManager.save()

                reportSessions[player.uniqueId] = UUID.fromString(uuid)

                reportMSG(
                    commandSource as com.velocitypowered.api.proxy.Player, ConfigManager.report.reportedPlayers[uuid]
                )
            })
        }
        literalArgument("submit") {
            executes(CommandExecutor { sender, args ->
                val session = reportSessions[(sender as com.velocitypowered.api.proxy.Player).uniqueId]
                if (session == null) {
                    sender.sendMessage(mm.deserialize("<color:#ff978a>You currently have no report session open.</color>"))
                    return@CommandExecutor
                }
                reportSessions.remove((sender as com.velocitypowered.api.proxy.Player).uniqueId)

                sender.sendMessage(mm.deserialize("<color:#85ff91>Your report will now be processed.</color> <color:#ff6e88>Thank you for your support.</color>\n<gray>We may contact you if we have any questions. Join our Discord for this. \n\n(https://crystopia.link/discord)</gray>"))
            })
        }
        literalArgument("cancel") {
            executes(CommandExecutor { sender, args ->
                val session = reportSessions[(sender as com.velocitypowered.api.proxy.Player).uniqueId]
                if (session == null) {
                    sender.sendMessage(mm.deserialize("<color:#ff978a>You currently have no report session open.</color>"))
                    return@CommandExecutor
                }
                ConfigManager.report.reportedPlayers.remove(reportSessions.toString())
                ConfigManager.save()

                reportSessions.remove((sender as com.velocitypowered.api.proxy.Player).uniqueId)

                sender.sendMessage(mm.deserialize("<color:#6baa75>Your report has been canceled and not sent!</color>"))
            })
        }
        literalArgument("create") {
            literalArgument("message") {
                textArgument("message") {
                    executes(CommandExecutor { sender, args ->
                        val session = reportSessions[(sender as com.velocitypowered.api.proxy.Player).uniqueId]
                        if (session == null) {
                            sender.sendMessage(mm.deserialize("<color:#ff978a>You currently have no report session open.</color>"))
                            return@CommandExecutor
                        }

                        val message = args[0] as String

                        ConfigManager.report.reportedPlayers[session.toString()]!!.message = message
                        ConfigManager.save()

                        reportMSG(sender, ConfigManager.report.reportedPlayers[session.toString()])
                        sender.sendMessage(mm.deserialize("<color:#8fff9a>You have added a reason to the report.</color>"))
                    })
                }
            }
            literalArgument("discord") {
                stringArgument("username") {
                    executes(CommandExecutor { sender, args ->
                        val session = reportSessions[(sender as com.velocitypowered.api.proxy.Player).uniqueId]
                        if (session == null) {
                            sender.sendMessage(mm.deserialize("<color:#ff978a>You currently have no report session open.</color>"))
                            return@CommandExecutor
                        }

                        val username = args[0] as String

                        ConfigManager.report.reportedPlayers[session.toString()]!!.playerDiscordUsername = username
                        ConfigManager.save()

                        reportMSG(sender, ConfigManager.report.reportedPlayers[session.toString()])
                        sender.sendMessage(mm.deserialize("<color:#8fff9a>You have saved your Discord our.</color>"))
                    })
                }
            }
            literalArgument("images") {
                literalArgument("add") {
                    textArgument("url") {
                        executes(CommandExecutor { sender, args ->
                            val session = reportSessions[(sender as com.velocitypowered.api.proxy.Player).uniqueId]
                            if (session == null) {
                                sender.sendMessage(mm.deserialize("<color:#ff978a>You currently have no report session open.</color>"))
                                return@CommandExecutor
                            }
                            val url = args[0]

                            ConfigManager.report.reportedPlayers[session.toString()]!!.images.add(url.toString())
                            ConfigManager.save()

                            reportMSG(sender, ConfigManager.report.reportedPlayers[session.toString()])
                            sender.sendMessage(mm.deserialize("<color:#f8ffc9>You have added an image. <gray>(This serves as a prood of your report)</gray></color>"))
                        })
                    }
                }
                literalArgument("remove") {
                    textArgument("url") {
                        executes(CommandExecutor { sender, args ->
                            val session = reportSessions[(sender as com.velocitypowered.api.proxy.Player).uniqueId]
                            if (session == null) {
                                sender.sendMessage(mm.deserialize("<color:#ff978a>You currently have no report session open.</color>"))
                                return@CommandExecutor
                            }
                            val url = args[0]

                            ConfigManager.report.reportedPlayers[session.toString()]!!.images.remove(url.toString())
                            ConfigManager.save()

                            reportMSG(sender, ConfigManager.report.reportedPlayers[session.toString()])
                            sender.sendMessage(mm.deserialize("<color:#f8ffc9>You have removed an image.</color>"))
                        })
                    }
                }
            }
            literalArgument("collaborators") {
                literalArgument("add") {
                    stringArgument("player") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                Main.instance.server.allPlayers.map { it.username }.toTypedArray()
                            })
                        executes(CommandExecutor { sender, args ->
                            val session = reportSessions[(sender as com.velocitypowered.api.proxy.Player).uniqueId]
                            if (session == null) {
                                sender.sendMessage(mm.deserialize("<color:#ff978a>You currently have no report session open.</color>"))
                                return@CommandExecutor
                            }
                            val collaborator = Main.instance.server.getPlayer(args[0] as String).get()

                            ConfigManager.report.reportedPlayers[session.toString()]!!.collaborators.add(collaborator.uniqueId.toString())
                            ConfigManager.save()

                            reportMSG(sender, ConfigManager.report.reportedPlayers[session.toString()])
                            sender.sendMessage(mm.deserialize("<color:#c9e4ff>You have added <color:#ffe6d1>${collaborator.username} <gray>(${collaborator.uniqueId})</gray></color> as a collaborator!</color> <gray>(He is also involved in the event)</gray>"))
                        })
                    }
                }
                literalArgument("remove") {
                    stringArgument("player") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                Main.instance.server.allPlayers.map { it.username }.toTypedArray()
                            })
                        executes(CommandExecutor { sender, args ->
                            val session = reportSessions[(sender as com.velocitypowered.api.proxy.Player).uniqueId]
                            if (session == null) {
                                sender.sendMessage(mm.deserialize("<color:#ff978a>You currently have no report session open.</color>"))
                                return@CommandExecutor
                            }
                            val collaborator = Main.instance.server.getPlayer(args[0] as String).get()

                            ConfigManager.report.reportedPlayers[session.toString()]!!.collaborators.remove(collaborator.uniqueId.toString())
                            ConfigManager.save()

                            reportMSG(sender, ConfigManager.report.reportedPlayers[session.toString()])
                            sender.sendMessage(mm.deserialize("<color:#c9e4ff>You have removed <color:#ffe6d1>${collaborator.username} <gray>(${collaborator.uniqueId})</gray></color> as a collaborator!</color> <gray>(He is also involved <b>not</b> in the event)</gray>"))
                        })
                    }
                }
            }
        }
    }

    fun getPlayerName(uuid: String): String {
        return Main.instance.server.getPlayer(UUID.fromString(uuid)).map { it.username }.orElse("Unknown")
    }

    fun reportMSG(player: com.velocitypowered.api.proxy.Player, reportData: PlayerReportData?) {
        var message = reportData!!.message
        var reportedPlayer = Main.instance.server.getPlayer(UUID.fromString(reportData!!.playerUUID)).get().username
        var id = reportData!!.uuid
        var discord = reportData!!.playerDiscordUsername
        var collaborators = reportData!!.collaborators.mapNotNull { uuid ->
            Main.instance.server.getPlayer(UUID.fromString(uuid))?.get()!!.username
        }
        var images = reportData!!.images.toString()

        if (message.isEmpty()) message = "<gray>(CLICK HERE TO ADD)</gray>"
        if (discord.isEmpty()) discord = "<gray>(CLICK HERE TO ADD)</gray>"
        if (collaborators.isEmpty()) collaborators = listOf("<gray>(CLICK HERE TO ADD)</gray>")
        if (images.isEmpty()) images = "<gray>(CLICK HERE TO ADD)</gray>"

        player.sendMessage(
            mm.deserialize(
                "<i><color:#e7ffa6>Player Report <u>${reportedPlayer}</u></color></i>\n\n<click:suggest_command:'/report create message'><st><gray>-</gray></st> <color:#fffb91>Message:</color> <gray>${message}</gray></click>\n<click:suggest_command:'/report create discord'><st><gray>-</gray></st> <color:#fffb91>Discord-Username:</color> <gray>${discord}</gray></click>\n<click:suggest_command:'/report create collaborators'><st><gray>-</gray></st> <color:#fffb91>Collaborators:</color> <gray>${
                    collaborators.joinToString(
                        ", "
                    )
                }</gray></click>\n<click:suggest_command:'/report create images'><st><gray>-</gray></st> <color:#fffb91>Images:</color> <gray>${images}</gray></click>\n<st><gray>-</gray></st> <color:#fffb91>Report-ID:</color> <gray>${id}</gray>\n\n  <color:#9dff9c><b><click:suggest_command:'/report submit'>[SUBMIT THE REPORT]</click></b></color> <b><color:#ff7d88><click:suggest_command:'/report cancel'>[CANCEL REPORT]</click></color></b>\n"
            )
        )
    }

}

