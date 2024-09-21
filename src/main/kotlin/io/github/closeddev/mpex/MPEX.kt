package io.github.closeddev.mpex

import io.github.closeddev.mpex.game.commands.GameCommand
import io.github.closeddev.mpex.commands.TestCommand
import io.github.closeddev.mpex.events.ProjectileEvents
import io.github.closeddev.mpex.events.FireEvent
import io.github.closeddev.mpex.events.ReloadWeaponEvent
import io.github.closeddev.mpex.events.WallJumpEvent
import io.github.closeddev.mpex.ui.WeaponDataView
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MPEX : JavaPlugin() {
    companion object {
        internal lateinit var instance: MPEX
    }

    override fun onEnable() {
        instance = this

        getCommand("test")?.setExecutor(TestCommand())
        getCommand("game")?.setExecutor(GameCommand())

        getCommand("game")?.tabCompleter = GameCommand()

        server.pluginManager.registerEvents(WallJumpEvent(), this)
        server.pluginManager.registerEvents(FireEvent(), this)
        server.pluginManager.registerEvents(ProjectileEvents(), this)
        server.pluginManager.registerEvents(ReloadWeaponEvent(), this)

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, WeaponDataView(), 0L, 2L)

        logger.info("Minecraft APEX Enabled.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
