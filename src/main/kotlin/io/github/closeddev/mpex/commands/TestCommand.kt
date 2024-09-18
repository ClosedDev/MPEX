package io.github.closeddev.mpex.commands

import io.github.closeddev.mpex.weapons.Carbine
import io.github.closeddev.mpex.weapons.Kraber
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val p = sender as Player
        p.sendMessage("Hello World!")

        p.inventory.addItem(Carbine().itemStack)
        p.inventory.addItem(Kraber().itemStack)
        return true
    }
}