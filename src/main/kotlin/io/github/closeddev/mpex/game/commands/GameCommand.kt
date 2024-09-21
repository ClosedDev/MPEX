package io.github.closeddev.mpex.game.commands

import io.github.closeddev.mpex.game.classes.Game
import io.github.closeddev.mpex.game.classes.Map
import io.github.closeddev.mpex.game.games.BattleRoyaleGame
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
                    if (args.size > 2) {
                        val map = Map.getMapInstance(args[1]) ?: return false
                        val players = args.sliceArray(2..args.lastIndex).mapNotNull { Bukkit.getPlayer(it) }.distinct()

                        val game = BattleRoyaleGame(map, players)
                        if (!game.isAvailable) return false

                        game.start()
                        return true
                    }
                }
                "stop" -> {
                    if (args.size > 1) {
                        val game = Game.getInstance(args[1]) ?: return false
                        game.stop()
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
            1 -> listOf("start", "stop")
            2 -> {
                when (args[0]) {
                    "start" -> Map.MAP_LIST.map { it.id }
                    "stop" -> Game.currentPlaying.map { it.id.toString() }
                    else -> listOf()
                }
            }
            else -> {
                if (args[0] == "start") Bukkit.getServer().onlinePlayers.map { it.name }.toList()
                else listOf()
            }
        }
    }
}