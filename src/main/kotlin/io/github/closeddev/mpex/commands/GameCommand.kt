package io.github.closeddev.mpex.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameCommand : CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (p0 is Player) {
            val player = p0
            // TODO: Game 인스턴스 생성, 초기화 (Game.Game 구현 우선)
        }
        return false
    }
}