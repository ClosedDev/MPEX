package io.github.closeddev.mpex

import io.github.closeddev.mpex.commands.TestCommand
import io.github.closeddev.mpex.events.WallJump
import org.bukkit.plugin.java.JavaPlugin

class MPEX : JavaPlugin() {
    companion object {
        internal lateinit var instance: MPEX
    }

    override fun onEnable() {
        instance = this

        getCommand("test")?.setExecutor(TestCommand())
        server.pluginManager.registerEvents(WallJump(), this)
        logger.info("Minecraft APEX Enabled.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
