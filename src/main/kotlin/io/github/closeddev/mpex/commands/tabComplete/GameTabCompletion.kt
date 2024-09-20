package io.github.closeddev.mpex.commands.tabComplete

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class GameTabCompletion : TabCompleter {
    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        args: Array<out String>?
    ): MutableList<String>? {
        if (args == null) return null

        return when {
//            args.size == 1 -> Game.getGamesID() // TODO: 게임 ID 리스트
//            args.size == 2 -> Maps.getMapList() // TODO: 맵 리스트
            args.size in 3..4 -> {
                Bukkit.getServer().onlinePlayers.map { it.name }.toMutableList()
            }
            else -> mutableListOf()
        }
    }
}
