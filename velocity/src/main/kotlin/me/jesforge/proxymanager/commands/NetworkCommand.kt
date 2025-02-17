package me.jesforge.proxymanager.commands

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.kotlindsl.*
import me.jesforge.proxymanager.Main
import me.jesforge.proxymanager.config.BannedPlayer
import me.jesforge.proxymanager.config.Command
import me.jesforge.proxymanager.config.CommandPerissionData
import me.jesforge.proxymanager.config.ConfigManager
import me.jesforge.proxymanager.config.ConfigManager.player
import me.jesforge.proxymanager.utils.ParseTime
import me.jesforge.proxymanager.utils.PlayerCache
import me.jesforge.proxymanager.utils.TypeVariabeln
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.checkerframework.checker.units.qual.mm
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

class NetworkCommand {
    val mm = MiniMessage.miniMessage()

    lateinit var serverSuggestion: String

    val command = commandTree("network") {
        withPermission("crystopia.commands.network")
        literalArgument("reports") {
            literalArgument("management") {
                literalArgument("list") {
                    executes(CommandExecutor { sender, args ->
                        val reports = ConfigManager.report.reportedPlayers

                        if (reports.isEmpty()) {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No reports found."))
                            return@CommandExecutor
                        }

                        val miniMessage = MiniMessage.miniMessage()
                        val reportMessages = mutableListOf<String>()

                        for (report in reports) {
                            val reportedPlayer = ReportCommand().getPlayerName(report.value.playerUUID)
                            val reporter = ReportCommand().getPlayerName(report.value.reporterUUID)

                            val reportMessage = """
                            <click:run_command:'/network reports management management view ${report.value.uuid}'>
                            <st><gray>──────────────</gray></st>
                            <color:#ffff9e><b>📌 Report:</b></color>
                            <gray><st>-</st> <color:#fff8c2>Player:</color> $reportedPlayer</gray>
                            <gray><st>-</st> <color:#fff8c2>Discord-Username:</color> ${report.value.playerDiscordUsername}</gray>
                            <gray><st>-</st> <color:#fff8c2>Reported by:</color> $reporter</gray>
                            <gray><st>-</st> <color:#fff8c2>Created at:</color> ${
                                ParseTime().parseDuration(report.value.createdAt).toDays()
                            } Days ago</gray>
                            <gray><st>-</st> <color:#fff8c2>Message:</color> ${report.value.message}</gray>
                            <st><gray>──────────────</gray></st>
                            </click>
                            """.trimIndent()

                            reportMessages.add(reportMessage)
                        }

                        sender.sendMessage(miniMessage.deserialize(reportMessages.joinToString("\n")))
                    })
                }
                literalArgument("claim") {
                    stringArgument("uuid") {
                        executes(CommandExecutor { sender, args ->
                            val report = ConfigManager.report.reportedPlayers[args[0]]

                            if (report == null) {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No reports found."))
                                return@CommandExecutor
                            }

                            if (report!!.claim != null) {
                                sender.sendMessage(mm.deserialize("<color:#ff8280>This reports is already claimed!</color>"))
                                return@CommandExecutor
                            }

                            val uuid = report.uuid

                            report!!.claim = (sender as com.velocitypowered.api.proxy.Player).uniqueId.toString()
                            ConfigManager.save()

                            val reportMessage = """
                            <st><gray>──────────────</gray></st>
                            <color:#ffff9e><b>📌 Report:</b></color>
                            <gray><st>-</st> <color:#fff8c2>Player:</color> ${ReportCommand().getPlayerName(report.playerUUID)}</gray>
                            <gray><st>-</st> <color:#fff8c2>Discord-Username:</color> ${report.playerDiscordUsername}</gray>
                            <gray><st>-</st> <color:#fff8c2>Reported by:</color> ${ReportCommand().getPlayerName(report.reporterUUID)}</gray>
                            <gray><st>-</st> <color:#fff8c2>Created at:</color> ${
                                ParseTime().parseDuration(report.createdAt).toDays()
                            } Days ago</gray>
                            <gray><st>-</st> <color:#fff8c2>Message:</color> ${report.message}</gray>
                            <gray><st>-</st> <color:#fff8c2>Collaborators:</color> ${
                                if (report.collaborators.isNullOrEmpty()) {
                                    "<color:#ff0000>No collaborators</color>"
                                } else {
                                    report.collaborators.joinToString(" ") { collaborator ->
                                        "${ReportCommand().getPlayerName(collaborator)}"
                                    }
                                }
                            }</gray>

    <gray><st>-</st> <color:#fff8c2>Images:</color> ${
                                if (report.images.isNullOrEmpty()) {
                                    "<color:#ff0000>No images available</color>"
                                } else {
                                    report.images.joinToString(" ") { image ->
                                        "<click:open_url:'$image'>$image</click>"
                                    }
                                }
                            }</gray>
                            <click:suggest_command:'/network reports management management claim ${report.uuid}'><gray><st>-</st> <color:#fff8c2>Team Claim by:</color> ${
                                ReportCommand().getPlayerName(
                                    report.claim.toString()
                                )
                            }</gray></click>
                        
                        <b><color:#809c96><click:suggest_command:'/network reports management management archiv ${uuid}'>[ARCHIV]</click></color> <color:#9cff91><click:suggest_command:'/network reports management management resolve ${uuid}'>[RESOLVE]</click></color> <color:#ff6663><click:suggest_command:'/network reports management management delete ${uuid}'>[DELETE]</click></color></b>
                            <st><gray>──────────────</gray></st>
                          
                            """.trimIndent()

                            sender.sendMessage(mm.deserialize(reportMessage))
                            sender.sendMessage(mm.deserialize("<color:#bfbfbf>Report claimed to you (${(sender as com.velocitypowered.api.proxy.Player).username.toString()}) - Report ID ${report.uuid}</color>"))
                        })
                    }
                }
                literalArgument("delete") {
                    stringArgument("uuid") {
                        executes(CommandExecutor { sender, args ->
                            val report = ConfigManager.report.reportedPlayers[args[0]]

                            if (report == null) {
                                sender.sendMessage(mm.deserialize("<color:#ff8280>This reports found.</color>"))
                            }

                            ConfigManager.report.reportedPlayers.remove(args[0])
                            ConfigManager.save()

                            sender.sendMessage(mm.deserialize("<color:#a5ff85>The report has now been deleted!</color>"))
                        })
                    }
                }
                literalArgument("resolve") {
                    stringArgument("uuid") {
                        executes(CommandExecutor { sender, args ->
                            val report = ConfigManager.report.reportedPlayers[args[0]]

                            if (report == null) {
                                sender.sendMessage(mm.deserialize("<color:#ff8280>This reports found.</color>"))
                            }

                            report!!.resolve = true
                            ConfigManager.save()

                            sender.sendMessage(mm.deserialize("<color:#a5ff85>The report has now been resolved!</color> <gray>(The player will get a notification)</gray>"))
                        })
                    }
                }
                literalArgument("archiv") {
                    stringArgument("uuid") {
                        executes(CommandExecutor { sender, args ->
                            val report = ConfigManager.report.reportedPlayers[args[0]]

                            if (report == null) {
                                sender.sendMessage(mm.deserialize("<color:#ff8280>This reports found.</color>"))
                            }

                            ConfigManager.report.reportArchiv.put(
                                report!!.uuid, report
                            )
                            ConfigManager.report.reportedPlayers.remove(args[0])
                            ConfigManager.save()

                            sender.sendMessage(mm.deserialize("<color:#a5ff85>The report has now been archived!</color>"))
                        })
                    }
                    literalArgument("list") {
                        executes(CommandExecutor { sender, args ->
                            val reports = ConfigManager.report.reportArchiv

                            if (reports.isEmpty()) {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No reports found."))
                                return@CommandExecutor
                            }


                            val miniMessage = MiniMessage.miniMessage()
                            val reportMessages = mutableListOf<String>()

                            for (report in reports) {
                                val reportedPlayer = ReportCommand().getPlayerName(report.value.playerUUID)
                                val reporter = ReportCommand().getPlayerName(report.value.reporterUUID)

                                var claimed = report.value.claim
                                if (claimed == null) claimed = "none" else claimed = ReportCommand().getPlayerName(
                                    report.value.claim.toString()
                                )

                                val reportMessage = """
    <st><gray>──────────────</gray></st>
    <color:#ffff9e><b>📌 Report Archiv:</b></color>
    <gray><st>-</st> <color:#fff8c2>Player:</color> $reportedPlayer</gray>
    <gray><st>-</st> <color:#fff8c2>Discord-Username:</color> ${report.value.playerDiscordUsername}</gray>
    <gray><st>-</st> <color:#fff8c2>Reported by:</color> $reporter</gray>
    <gray><st>-</st> <color:#fff8c2>Created at:</color> ${
                                    ParseTime().parseDuration(report.value.createdAt).toDays()
                                } Days ago</gray>
    <gray><st>-</st> <color:#fff8c2>Message:</color> ${report.value.message}</gray>
    
    <gray><st>-</st> <color:#fff8c2>Collaborators:</color> ${
                                    if (report.value.collaborators.isNullOrEmpty()) {
                                        "<color:#ff0000>No collaborators</color>"
                                    } else {
                                        report.value.collaborators.joinToString(" ") { collaborator ->
                                            ReportCommand().getPlayerName(collaborator)
                                        }
                                    }
                                }</gray>

    <gray><st>-</st> <color:#fff8c2>Images:</color> ${
                                    if (report.value.images.isNullOrEmpty()) {
                                        "<color:#ff0000>No images available</color>"
                                    } else {
                                        report.value.images.joinToString(" ") { image ->
                                            "<click:open_url:'$image'>$image</click>"
                                        }
                                    }
                                }</gray>
    <gray><st>-</st> <color:#fff8c2>Team Claim by:</color> $claimed</gray>
    <st><gray>──────────────</gray></st>
""".trimIndent()
                                reportMessages.add(reportMessage)
                            }

                            sender.sendMessage(miniMessage.deserialize(reportMessages.joinToString("\n")))
                        })
                    }
                }
                literalArgument("view") {
                    stringArgument("uuid") {
                        executes(CommandExecutor { sender, args ->
                            val report = ConfigManager.report.reportedPlayers[args[0]]

                            if (report == null) {
                                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No reports found."))
                                return@CommandExecutor
                            }

                            val miniMessage = MiniMessage.miniMessage()

                            val uuid = report.uuid
                            val reportedPlayer = ReportCommand().getPlayerName(report.playerUUID)
                            val reporter = ReportCommand().getPlayerName(report.reporterUUID)

                            var claimed = report.claim
                            if (claimed == null) claimed = "(CLICK TO CLAIM)" else claimed =
                                ReportCommand().getPlayerName(
                                    report.claim.toString()
                                )

                            val reportMessage = """
    <st><gray>──────────────</gray></st>
    <color:#ffff9e><b>📌 Report:</b></color>
    <gray><st>-</st> <color:#fff8c2>Player:</color> $reportedPlayer</gray>
    <gray><st>-</st> <color:#fff8c2>Discord-Username:</color> ${report.playerDiscordUsername}</gray>
    <gray><st>-</st> <color:#fff8c2>Reported by:</color> $reporter</gray>
    <gray><st>-</st> <color:#fff8c2>Created at:</color> ${
                                ParseTime().parseDuration(report.createdAt).toDays()
                            } Days ago</gray>
    <gray><st>-</st> <color:#fff8c2>Message:</color> ${report.message}</gray>
    
    <gray><st>-</st> <color:#fff8c2>Collaborators:</color> ${
                                if (report.collaborators.isNullOrEmpty()) {
                                    "<color:#ff0000>No collaborators</color>"
                                } else {
                                    report.collaborators.joinToString(" ") { collaborator ->
                                        "${ReportCommand().getPlayerName(collaborator)}"
                                    }
                                }
                            }</gray>

    <gray><st>-</st> <color:#fff8c2>Images:</color> ${
                                if (report.images.isNullOrEmpty()) {
                                    "<color:#ff0000>No images available</color>"
                                } else {
                                    report.images.joinToString(" ") { image ->
                                        "<click:open_url:'$image'>$image</click>"
                                    }
                                }
                            }</gray>

    <click:suggest_command:'/network reports management management claim ${report.uuid}'><gray><st>-</st> <color:#fff8c2>Team Claim by:</color> $claimed</gray></click>
    
    <b><color:#809c96><click:suggest_command:'/network reports management management archiv ${uuid}'>[ARCHIV]</click></color> <color:#9cff91><click:suggest_command:'/network reports management management resolve ${uuid}'>[RESOLVE]</click></color> <color:#ff6663><click:suggest_command:'/network reports management management delete ${uuid}'>[DELETE]</click></color></b>
    <st><gray>──────────────</gray></st>
""".trimIndent()



                            sender.sendMessage(miniMessage.deserialize(reportMessage))
                        })
                    }
                }
            }
        }
        literalArgument("moderation") {
            literalArgument("player") {
                literalArgument("bans") {
                    stringArgument("player-uuid") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                ConfigManager.ban.bannedplayers.flatMap { player ->
                                    ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                        if (mcplayer.uuid == player.value.uuid) {
                                            mcplayer.name
                                        } else {
                                            player.value.uuid
                                        }
                                    }
                                }.toTypedArray()
                            })
                        executes(CommandExecutor { commandSource, commandArguments ->
                            var playerUUID = try {
                                UUID.fromString(commandArguments[0].toString())
                            } catch (e: IllegalArgumentException) {
                                ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                    if (mcplayer.name == commandArguments[0]) {
                                        mcplayer.uuid
                                    } else return@CommandExecutor
                                }
                            }.toString().replace("[", "").replace("]", "")

                            val ban = ConfigManager.ban.bannedplayers[playerUUID]
                            val player =
                                ConfigManager.mcPlayerCache.mcPlayers.firstOrNull { it.uuid == ban?.uuid }?.name
                                    ?: "Unknown Player"

                            if (ban == null) {
                                commandSource.sendMessage(mm.deserialize("<color:#ff0000>No bans found for this player.</color>"))
                                return@CommandExecutor
                            }

                            commandSource.sendMessage(
                                mm.deserialize(
                                    """
        <color:#feffab><i><b>Ban Overview</b></i></color>
        <gray><st>-</st></gray> <color:#fff27d>Player:</color> <color:#97abbf>${player}</color> <gray>(${ban?.uuid})</gray>
        <gray><st>-</st></gray> <color:#fff27d>UUID:</color> <color:#97abbf>${ban?.uuid}</color>
        <gray><st>-</st></gray> <color:#fff27d>Ban UUID:</color> <color:#97abbf>${ban?.banUUID}</color>
        <gray><st>-</st></gray> <color:#fff27d>Ban ID:</color> <color:#97abbf>${ban?.banIntID}</color>
        <gray><st>-</st></gray> <color:#fff27d>Reason:</color> <color:#97abbf>${ban?.reason}</color>
        <gray><st>-</st></gray> <color:#fff27d>Ended:</color> <color:#97abbf>${ban?.ended}</color>
        <gray><st>-</st></gray> <color:#fff27d>Created at:</color> <color:#97abbf>${
                                        ParseTime().parseDuration(ban?.createdAt ?: "unknown").toDays()
                                    } days ago</color>
        """
                                )
                            )

                        })
                    }
                }
                literalArgument("archiv") {
                    literalArgument("ban") {
                        stringArgument("player-uuid") {
                            replaceSuggestions(
                                ArgumentSuggestions.strings {
                                    ConfigManager.ban.banArchiv.flatMap { player ->
                                        ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                            if (mcplayer.uuid == player.uuid) {
                                                mcplayer.name
                                            } else {
                                                player.uuid
                                            }
                                        }
                                    }.toTypedArray()
                                })
                            executes(CommandExecutor { commandSource, commandArguments ->
                                var playerUUID = try {
                                    UUID.fromString(commandArguments[0].toString())
                                } catch (e: IllegalArgumentException) {
                                    ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                        if (mcplayer.name == commandArguments[0]) {
                                            mcplayer.uuid
                                        } else return@CommandExecutor
                                    }
                                }.toString().replace("[", "").replace("]", "")

                                val bans = ConfigManager.ban.banArchiv.filter { it.uuid == playerUUID }
                                if (bans.isNotEmpty()) {
                                    bans.forEach { ban ->
                                        val player =
                                            ConfigManager.mcPlayerCache.mcPlayers.firstOrNull { it.uuid == ban.uuid }?.name
                                                ?: "Unknown Player"
                                        commandSource.sendMessage(
                                            mm.deserialize(
                                                """
                <color:#feffab><i><b>Ban Overview</b></i></color>
                <gray><st>-</st></gray> <color:#fff27d>Player:</color> <color:#97abbf>${player}</color> <gray>(${ban.uuid})</gray>
                <gray><st>-</st></gray> <color:#fff27d>UUID:</color> <color:#97abbf>${ban.uuid}</color>
                <gray><st>-</st></gray> <color:#fff27d>Ban UUID:</color> <color:#97abbf>${ban.banUUID}</color>
                <gray><st>-</st></gray> <color:#fff27d>Ban ID:</color> <color:#97abbf>${ban.banIntID}</color>
                <gray><st>-</st></gray> <color:#fff27d>Reason:</color> <color:#97abbf>${ban.reason}</color>
                <gray><st>-</st></gray> <color:#fff27d>Ended:</color> <color:#97abbf>${ban.ended}</color>
                <gray><st>-</st></gray> <color:#fff27d>Created at:</color> <color:#97abbf>${
                                                    ParseTime().parseDuration(
                                                        ban.createdAt ?: "unknown"
                                                    ).toDays()
                                                } days ago</color>
                """
                                            )
                                        )
                                    }
                                } else {
                                    commandSource.sendMessage(mm.deserialize("<color:#ff0000>No bans found for this player.</color>"))
                                }


                            })
                        }
                    }
                    literalArgument("report") {
                        stringArgument("player-uuid") {
                            replaceSuggestions(
                                ArgumentSuggestions.strings {
                                    ConfigManager.report.reportArchiv.flatMap { player ->
                                        ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                            if (mcplayer.uuid == player.value.playerUUID) {
                                                mcplayer.name
                                            } else {
                                                player.value.playerUUID
                                            }
                                        }
                                    }.toTypedArray()
                                })
                            executes(CommandExecutor { commandSource, commandArguments ->
                                var playerUUID = try {
                                    UUID.fromString(commandArguments[0].toString())
                                } catch (e: IllegalArgumentException) {
                                    ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                        if (mcplayer.name == commandArguments[0]) {
                                            mcplayer.uuid
                                        } else return@CommandExecutor
                                    }
                                }.toString().replace("[", "").replace("]", "")


                                val reports =
                                    ConfigManager.report.reportArchiv.values.filter { it.playerUUID == playerUUID }
                                if (reports.isNotEmpty()) {
                                    reports.forEach { report ->
                                        val player =
                                            ConfigManager.mcPlayerCache.mcPlayers.firstOrNull { it.uuid == report.playerUUID }?.name
                                                ?: "Unknown Player"
                                        commandSource.sendMessage(
                                            mm.deserialize(
                                                """
                <color:#feffab><i><b>Report Overview</b></i></color>
                <gray><st>-</st></gray> <color:#fff27d>Player:</color> <color:#97abbf>${player}</color> <gray>(${report.playerUUID ?: "Unknown UUID"})</gray>
                <gray><st>-</st></gray> <color:#fff27d>UUID:</color> <color:#97abbf>${report.playerUUID ?: "Unknown UUID"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Message:</color> <color:#97abbf>${report.message ?: "No message"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Images:</color> <color:#97abbf>${report.images?.joinToString(", ") ?: "No images"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Collaborators:</color> <color:#97abbf>${
                                                    report.collaborators?.mapNotNull { uuid ->
                                                        ConfigManager.mcPlayerCache.mcPlayers.firstOrNull { it.uuid == uuid }?.name
                                                    }?.joinToString(", ") ?: "No collaborators"
                                                }</color>
                <gray><st>-</st></gray> <color:#fff27d>Player Discord Username:</color> <color:#97abbf>${report.playerDiscordUsername ?: "No Discord username"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Reporter UUID:</color> <color:#97abbf>${
                                                    PlayerCache().getPlayerfromCache(
                                                        report.reporterUUID.toString()
                                                    ) ?: "No claim" ?: "Unknown reporter UUID"
                                                }</color>
                <gray><st>-</st></gray> <color:#fff27d>Claim:</color> <color:#97abbf>${
                                                    PlayerCache().getPlayerfromCache(
                                                        report.claim.toString()
                                                    ) ?: "No claim" ?: "No claim"
                                                }</color>
                <gray><st>-</st></gray> <color:#fff27d>Resolved:</color> <color:#97abbf>${report.resolve?.toString() ?: "Not Resolved"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Notified:</color> <color:#97abbf>${report.notified?.toString() ?: "Not notified"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Created at:</color> <color:#97abbf>${
                                                    ParseTime().parseDuration(
                                                        report.createdAt.toString()
                                                    ).toDays()
                                                } days ago</color>
                """
                                            )
                                        )
                                    }
                                } else {
                                    commandSource.sendMessage(mm.deserialize("<color:#ff0000>No reports found for this player.</color>"))
                                }
                            })
                        }
                    }
                }
                literalArgument("reports") {
                    stringArgument("player-uuid") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                ConfigManager.report.reportedPlayers.flatMap { player ->
                                    ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                        if (mcplayer.uuid == player.value.playerUUID) {
                                            mcplayer.name
                                        } else {
                                            player.value.playerUUID
                                        }
                                    }
                                }.toTypedArray()
                            })
                        executes(CommandExecutor { commandSource, commandArguments ->
                            var playerUUID = try {
                                UUID.fromString(commandArguments[0].toString())
                            } catch (e: IllegalArgumentException) {
                                ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                    if (mcplayer.name == commandArguments[0]) {
                                        mcplayer.uuid
                                    } else return@CommandExecutor
                                }
                            }.toString().replace("[", "").replace("]", "")

                            val reports =
                                ConfigManager.report.reportedPlayers.values.filter { it.playerUUID == playerUUID }
                            if (reports.isNotEmpty()) {
                                reports.forEach { report ->
                                    val player =
                                        ConfigManager.mcPlayerCache.mcPlayers.firstOrNull { it.uuid == report.playerUUID }?.name
                                            ?: "Unknown Player"
                                    commandSource.sendMessage(
                                        mm.deserialize(
                                            """
                <color:#feffab><i><b>Report Overview</b></i></color>
                <gray><st>-</st></gray> <color:#fff27d>Player:</color> <color:#97abbf>${player}</color> <gray>(${report.playerUUID ?: "Unknown UUID"})</gray>
                <gray><st>-</st></gray> <color:#fff27d>UUID:</color> <color:#97abbf>${report.playerUUID ?: "Unknown UUID"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Message:</color> <color:#97abbf>${report.message ?: "No message"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Images:</color> <color:#97abbf>${report.images?.joinToString(", ") ?: "No images"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Collaborators:</color> <color:#97abbf>${
                                                report.collaborators?.mapNotNull { uuid ->
                                                    ConfigManager.mcPlayerCache.mcPlayers.firstOrNull { it.uuid == uuid }?.name
                                                }?.joinToString(", ") ?: "No collaborators"

                                            }</color>
                <gray><st>-</st></gray> <color:#fff27d>Player Discord Username:</color> <color:#97abbf>${report.playerDiscordUsername ?: "No Discord username"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Reporter UUID:</color> <color:#97abbf>${
                                                PlayerCache().getPlayerfromCache(
                                                    report.reporterUUID.toString()
                                                ) ?: "No claim"
                                            }</color>
                <gray><st>-</st></gray> <color:#fff27d>Claim:</color> <color:#97abbf>${
                                                PlayerCache().getPlayerfromCache(
                                                    report.claim.toString()
                                                ) ?: "No claim"
                                            }</color>
                <gray><st>-</st></gray> <color:#fff27d>Resolved:</color> <color:#97abbf>${report.resolve?.toString() ?: "Not Resolved"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Notified:</color> <color:#97abbf>${report.notified?.toString() ?: "Not notified"}</color>
                <gray><st>-</st></gray> <color:#fff27d>Created at:</color> <color:#97abbf>${
                                                ParseTime().parseDuration(
                                                    report.createdAt.toString()
                                                ).toDays()
                                            } days ago</color>
                """
                                        )
                                    )
                                }
                            } else {
                                commandSource.sendMessage(mm.deserialize("<color:#ff0000>No reports found for this player.</color>"))
                            }

                        })
                    }
                }
            }
            literalArgument("unban") {
                stringArgument("player-uuid") {
                    replaceSuggestions(
                        ArgumentSuggestions.strings {
                            ConfigManager.ban.bannedplayers.flatMap { player ->
                                ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                    if (mcplayer.uuid == player.value.uuid) {
                                        mcplayer.name
                                    } else {
                                        player.value.uuid
                                    }
                                }
                            }.toTypedArray()
                        })
                    executes(CommandExecutor { commandSource, commandArguments ->
                        var playerUUID = try {
                            UUID.fromString(commandArguments[0].toString())
                        } catch (e: IllegalArgumentException) {
                            ConfigManager.mcPlayerCache.mcPlayers.map { mcplayer ->
                                if (mcplayer.name == commandArguments[0]) {
                                    mcplayer.uuid
                                } else return@CommandExecutor
                            }
                        }.toString().replace("[", "").replace("]", "")

                        ConfigManager.ban.bannedplayers[playerUUID]?.let { ConfigManager.ban.banArchiv.add(it) }
                        ConfigManager.ban.bannedplayers.remove(playerUUID.toString())
                        ConfigManager.save()

                        commandSource.sendMessage(
                            mm.deserialize("<color:#61ff79>This player can now join again! <gray>And his ban has been moved to the archive.</gray></color>")
                        )
                    })

                }
            }
            literalArgument("ban") {
                stringArgument("player") {
                    replaceSuggestions(
                        ArgumentSuggestions.strings {
                            Main.instance.server.allPlayers.map { it.username }.toTypedArray()
                        })
                    stringArgument("time") {
                        textArgument("message") {
                            executes(CommandExecutor { commandSource, commandArguments ->
                                val player = Main.instance.server.getPlayer(commandArguments[0] as String).get()
                                val message = commandArguments[2] as String
                                var time = ParseTime().parseTimeString(commandArguments[1] as String)


                                val banned = ConfigManager.ban.bannedplayers[player.uniqueId.toString()]
                                if (banned != null && !banned.ended) {
                                    commandSource.sendMessage(mm.deserialize("<color:#ff948c>This member is already banned!</color>"))
                                    return@CommandExecutor
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

                                commandSource.sendMessage(mm.deserialize("<color:#8aff8c>You have successfully created the playe <color:#ffd36e>${player.username}</color> for <gray>${time.toDays()}d</gray> <gray>${time.toHours()}h</gray> <gray>${time.toMinutes()}m</gray> </color>"))


                                val ban = ConfigManager.ban.bannedplayers[player.uniqueId.toString()]

                                val parts = ban!!.createdAt.split("-")
                                val formattedDate = "${parts[1]}-${parts[0]}"
                                player.disconnect(
                                    mm.deserialize(
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
                            })
                        }
                    }
                }
            }
            literalArgument("kick") {
                stringArgument("player") {
                    replaceSuggestions(
                        ArgumentSuggestions.strings {
                            Main.instance.server.allPlayers.map { it.username }.toTypedArray()
                        })
                    textArgument("message") {
                        executes(CommandExecutor { commandSource, commandArguments ->
                            val player = Main.instance.server.getPlayer(commandArguments[0] as String).get()
                            val message = commandArguments[2] as String

                            player.disconnect(mm.deserialize(message))

                        })
                    }
                }
            }
            literalArgument("warn") {
                literalArgument("title") {
                    stringArgument("player") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                Main.instance.server.allPlayers.map { it.username }.toTypedArray()
                            })
                        textArgument("title") {
                            textArgument("subTitle") {
                                executes(CommandExecutor { commandSource, commandArguments ->
                                    val player = Main.instance.server.getPlayer(commandArguments[0] as String).get()
                                    val title = commandArguments[2] as String
                                    var subTitle = commandArguments[3] as String

                                    player.showTitle(Title.title(mm.deserialize(title), mm.deserialize(subTitle)))

                                })
                            }
                        }
                    }
                }
                literalArgument("message") {
                    stringArgument("player") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                Main.instance.server.allPlayers.map { it.username }.toTypedArray()
                            })
                        textArgument("message") {
                            executes(CommandExecutor { commandSource, commandArguments ->
                                val player = Main.instance.server.getPlayer(commandArguments[0] as String).get()
                                val message = commandArguments[2] as String

                                player.sendMessage(mm.deserialize(message))

                            })
                        }
                    }
                }
            }
        }
        literalArgument("reload") {
            executes(CommandExecutor { commandSender, commandArguments ->
                ConfigManager.reload()
                commandSender.sendMessage(mm.deserialize("<color:#c3ffbf>All data has been reloaded.</color>"))

            })
        }
        literalArgument("version") {
            literalArgument("add") {
                stringArgument("version") {
                    integerArgument("protocol") {
                        executes(CommandExecutor { commandSender, commandArguments ->
                            ConfigManager.settings.serverData.versionData.version.add(commandArguments[0].toString())
                            ConfigManager.settings.serverData.versionData.protocols.add(
                                commandArguments[1].toString().toInt()
                            )
                            ConfigManager.save()

                            commandSender.sendMessage(mm.deserialize("<color:#9cffb1>The version is now updated</color>"))
                        })
                    }
                }
            }
            literalArgument("remove") {
                stringArgument("version") {
                    integerArgument("protocol") {
                        executes(CommandExecutor { commandSender, commandArguments ->
                            ConfigManager.settings.serverData.versionData.version.remove(commandArguments[0].toString())
                            ConfigManager.settings.serverData.versionData.protocols.remove(
                                commandArguments[1].toString().toInt()
                            )
                            ConfigManager.save()

                            commandSender.sendMessage(mm.deserialize("<color:#9cffb1>The version is now updated</color>"))
                        })
                    }
                }
            }
        }
        literalArgument("playerLimit") {
            integerArgument("limit") {
                executes(CommandExecutor { commandSender, commandArguments ->
                    ConfigManager.settings.serverData.playerLimit = commandArguments[0] as Int
                    ConfigManager.save()

                    commandSender.sendMessage(mm.deserialize("<color:#faffc9>The number of network players is now ${commandArguments[0]}</color>"))
                })
            }
        }
        literalArgument("serverVersion") {
            stringArgument("version") {
                executes(CommandExecutor { commandSender, commandArguments ->
                    ConfigManager.settings.serverData.serverVersion = commandArguments[0] as String
                    ConfigManager.save()

                    commandSender.sendMessage(mm.deserialize("<color:#faffc9>Server Version Update ${commandArguments[0]}</color>"))
                })
            }
        }
        literalArgument("commands") {
            literalArgument("add") {
                stringArgument("server") {
                    replaceSuggestions(
                        ArgumentSuggestions.strings {
                            Main.instance.server.allServers.map {
                                serverSuggestion = it.serverInfo.name
                                it.serverInfo.name
                            }.toTypedArray()
                        })
                    stringArgument("permission") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                ConfigManager.commands.servers[serverSuggestion]?.permissions?.keys?.toTypedArray()
                                    ?: emptyArray()
                            })
                        textArgument("command") {
                            booleanArgument("tabComplete") {
                                executes(CommandExecutor { commandSender, commandArguments ->

                                    val server = commandArguments[0] as String
                                    val permission = commandArguments[1] as String
                                    val command = commandArguments[2] as String
                                    val tabComplete = commandArguments[3] as Boolean
                                    val config = ConfigManager.commands.servers.getOrPut(server) {
                                        CommandPerissionData(
                                            permissions = mutableMapOf()
                                        )
                                    }

                                    val permissionData = config.permissions.getOrPut(permission) {
                                        Command(
                                            command = mutableListOf(), tabComplete = mutableListOf()
                                        )
                                    }

                                    ConfigManager.save()

                                    if (config.permissions[permission]!!.command.contains(command)) {
                                        commandSender.sendMessage(mm.deserialize("<color:#ff6961>This command already exists!</color>"))
                                        return@CommandExecutor
                                    } else {
                                        config.permissions[permission]!!.command.add(command)
                                    }

                                    if (tabComplete) {
                                        if (config.permissions[permission]!!.tabComplete.contains(command)) {
                                            commandSender.sendMessage(mm.deserialize("<color:#ff6961>This command already exists! <gray>(tabComplete)<gray></color>"))
                                            return@CommandExecutor
                                        } else {
                                            config.permissions[permission]!!.tabComplete.add(command)
                                        }
                                    }

                                    ConfigManager.save()
                                })
                            }
                        }
                    }
                }
                literalArgument("ALL") {
                    stringArgument("permission") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                ConfigManager.commands.servers["ALL"]?.permissions?.keys?.toTypedArray() ?: emptyArray()
                            })
                        textArgument("command") {
                            booleanArgument("tabComplete") {
                                executes(CommandExecutor { commandSender, commandArguments ->

                                    val server = "ALL"
                                    val permission = commandArguments[0] as String
                                    val command = commandArguments[1] as String
                                    val tabComplete = commandArguments[2] as Boolean
                                    val config = ConfigManager.commands.servers.getOrPut(server) {
                                        CommandPerissionData(
                                            permissions = mutableMapOf()
                                        )
                                    }

                                    val permissionData = config.permissions.getOrPut(permission) {
                                        Command(
                                            command = mutableListOf(), tabComplete = mutableListOf()
                                        )
                                    }

                                    ConfigManager.save()

                                    if (config.permissions[permission]!!.command.contains(command)) {
                                        commandSender.sendMessage(mm.deserialize("<color:#ff6961>This command already exists!</color>"))
                                        return@CommandExecutor
                                    } else {
                                        config.permissions[permission]!!.command.add(command)
                                    }

                                    if (tabComplete) {
                                        if (config.permissions[permission]!!.tabComplete.contains(command)) {
                                            commandSender.sendMessage(mm.deserialize("<color:#ff6961>This command already exists! <gray>(tabComplete)<gray></color>"))
                                            return@CommandExecutor
                                        } else {
                                            config.permissions[permission]!!.tabComplete.add(command)
                                        }
                                    }

                                    ConfigManager.save()
                                })
                            }
                        }
                    }
                }
            }

            literalArgument("remove") {
                replaceSuggestions(
                    ArgumentSuggestions.strings {
                        Main.instance.server.allServers.map { it.serverInfo.name }.toTypedArray()
                    })
                stringArgument("server") {
                    replaceSuggestions(
                        ArgumentSuggestions.strings {
                            Main.instance.server.allServers.map {
                                serverSuggestion = it.serverInfo.name
                                it.serverInfo.name
                            }.toTypedArray()
                        })
                    stringArgument("permission") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                ConfigManager.commands.servers[serverSuggestion]?.permissions?.keys?.toTypedArray()
                                    ?: emptyArray()
                            })
                        textArgument("command") {
                            booleanArgument("tabComplete") {
                                executes(CommandExecutor { commandSender, commandArguments ->

                                    val server = commandArguments[0] as String
                                    val permission = commandArguments[1] as String
                                    val command = commandArguments[2] as String
                                    val tabComplete = commandArguments[3] as Boolean
                                    val config = ConfigManager.commands.servers[server]

                                    if (config!!.permissions.contains(permission)) {
                                        if (!config.permissions[permission]!!.command.contains(command)) {
                                            commandSender.sendMessage(mm.deserialize("<color:#ff6961>This command does not exist!</color>"))
                                            return@CommandExecutor
                                        } else {
                                            config.permissions[permission]!!.command.remove(command)
                                        }

                                        if (tabComplete) {
                                            if (!config.permissions[permission]!!.tabComplete.contains(command)) {
                                                commandSender.sendMessage(mm.deserialize("<color:#ff6961>This command does not exist!</color><gray>(tabComplete)<gray>"))
                                                return@CommandExecutor
                                            } else {
                                                config.permissions[permission]!!.tabComplete.remove(command)
                                            }
                                        }
                                    }

                                    ConfigManager.save()
                                })
                            }
                        }
                    }
                }
                literalArgument("ALL") {
                    stringArgument("permission") {
                        replaceSuggestions(
                            ArgumentSuggestions.strings {
                                ConfigManager.commands.servers["ALL"]?.permissions?.keys?.toTypedArray() ?: emptyArray()
                            })
                        textArgument("command") {
                            booleanArgument("tabComplete") {
                                executes(CommandExecutor { commandSender, commandArguments ->

                                    val server = "ALL"
                                    val permission = commandArguments[0] as String
                                    val command = commandArguments[1] as String
                                    val tabComplete = commandArguments[2] as Boolean
                                    val config = ConfigManager.commands.servers[server]

                                    if (config!!.permissions.contains(permission)) {
                                        if (!config.permissions[permission]!!.command.contains(command)) {
                                            commandSender.sendMessage(mm.deserialize("<color:#ff6961>This command does not exist!</color>"))
                                            return@CommandExecutor
                                        } else {
                                            config.permissions[permission]!!.command.remove(command)
                                        }

                                        if (tabComplete) {
                                            if (!config.permissions[permission]!!.tabComplete.contains(command)) {
                                                commandSender.sendMessage(mm.deserialize("<color:#ff6961>This command does not exist!</color><gray>(tabComplete)<gray>"))
                                                return@CommandExecutor
                                            } else {
                                                config.permissions[permission]!!.tabComplete.remove(command)
                                            }
                                        }
                                    }

                                    ConfigManager.save()
                                })
                            }
                        }
                    }
                }
            }

            literalArgument("list") {
                executes(CommandExecutor { commandSender, commandArguments ->

                    val config = ConfigManager.commands.servers.map { (serverName, serverData) ->
                        val permissionsList =
                            serverData.permissions.entries.joinToString("\n") { (permissionName, commandData) ->
                                val commandList =
                                    commandData.command.joinToString(", ") { "<gradient:#FF5733:#C70039>$it</gradient>" }
                                val tabCompleteList =
                                    commandData.tabComplete.joinToString(", ") { "<gradient:#900C3F:#581845>$it</gradient>" }

                                "<white>$permissionName</white> -> " + "<gray>Commands:</gray> [$commandList], " + "<gray>TabComplete:</gray> [$tabCompleteList]"
                            }

                        "<bold><gradient:#008080:#40E0D0>Server:</gradient> <gradient:#8A2BE2:#9370DB>$serverName</gradient></bold>\n$permissionsList"
                    }.joinToString("\n\n")

                    val message = mm.deserialize(config)

                    commandSender.sendMessage(message)

                })
            }
        }
    }
}

