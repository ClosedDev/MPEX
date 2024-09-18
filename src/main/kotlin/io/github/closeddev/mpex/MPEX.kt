package io.github.closeddev.mpex

import commands.TestCommand
import org.bukkit.plugin.java.JavaPlugin

class MPEX : JavaPlugin() {

    override fun onEnable() {
        getCommand("test")?.setExecutor(TestCommand())
        logger.info("Minecraft APEX Enabled.")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
