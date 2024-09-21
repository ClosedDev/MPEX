package io.github.closeddev.mpex.game.commands

import io.github.closeddev.mpex.game.classes.Game
import io.github.closeddev.mpex.game.classes.Map
import io.github.closeddev.mpex.game.games.BattleRoyaleGame
import io.github.closeddev.mpex.game.games.DeathMatchGame
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class GameCommand : CommandExecutor, TabCompleter {

    companion object {
        val instance = GameCommand()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            when(args[0]) {
                "start" -> {
                    if (args.size > 3) {
                        val mode = args[1]
                        val map = Map.getMapInstance(args[2]) ?: return false
                        val playerNames = args.sliceArray(3..args.lastIndex)

                        val game = when (mode) {
                            "BATTLE_ROYALE" -> {
                                val players = playerNames.mapNotNull { Bukkit.getPlayer(it) }.distinct()
                                BattleRoyaleGame(map, players)
                            }
                            "DEATH_MATCH" -> {
                                val pivot = "|"
                                val filtered = playerNames.filterNot { it == pivot }
                                val pivotIndex = playerNames.indexOf(pivot)
                                val (red, blue) = filtered.partition { playerNames.indexOf(it) < pivotIndex }

                                println("$red, $blue")

                                val redPlayers = red.mapNotNull { Bukkit.getPlayer(it) }.distinct()
                                val bluePlayers = blue.mapNotNull { Bukkit.getPlayer(it) }.distinct()

                                DeathMatchGame(map, redPlayers, bluePlayers)
                            }
                            else -> return false
                        }

                        if (!game.isAvailable) return false

                        game.start()
                        return true
                    }
                }
                "abort" -> {
                    if (args.size > 1) {
                        val game = Game.getInstance(args[1]) ?: return false
                        game.abort()
                        return true
                    }
                }
            }
        }

        return false
    }
    
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        if (args.isEmpty()) return listOf()

        return when (args.size) {
            1 -> listOf("start", "abort")
            2 -> {
                when (args[0]) {
                    "start" -> listOf("BATTLE_ROYALE", "DEATH_MATCH")
                    "abort" -> Game.currentPlaying.map { it.id.toString() }
                    else -> listOf()
                }
            }
            3 -> {
                if (args[0] == "start") Map.MAP_LIST.map { it.id }
                else listOf()
            }
            else -> {
                if (args[0] == "start") {
                    val playerList = Bukkit.getServer().onlinePlayers.map { it.name }.toList()
                    if (args[1] == "DEATH_MATCH") playerList.plus("|") else playerList
                }
                else listOf()
            }
        }
    }
}