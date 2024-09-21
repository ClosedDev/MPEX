package io.github.closeddev.mpex.game.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class GameCommand : CommandExecutor, TabCompleter {

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p0 is Player) {
            val player = p0
            // TODO: Game 인스턴스 생성, 초기화 (game.classes.Game 구현 우선)
        }
        return false
    }
    
    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        args: Array<out String>?
    ): List<String>? {
        if (args == null) return null

        return when (args.size) {
            1 -> listOf("start", "stop")
            2 -> {
                when (args[1]) {
                    "start" -> listOf("MAPS") // TODO: GET MAP ID
                    "stop" -> listOf("GAMES") // TODO: GET GAME ID
                    else -> listOf()
                }
            }
            in 3..4 -> {
                Bukkit.getServer().onlinePlayers.map { it.name }.toList()
            }
            else -> listOf()
        }
    }
}