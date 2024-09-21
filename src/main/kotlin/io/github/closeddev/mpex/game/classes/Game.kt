package io.github.closeddev.mpex.game.classes

import io.github.closeddev.mpex.MPEX
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import java.time.Duration
import java.util.*

class Game(
    val map: Map,
    val players : List<Player>,
    val id: UUID = UUID.randomUUID()) : Listener {

    companion object {
        val currentPlaying = mutableListOf<Game>()

        fun getInstance(id: String): Game? {
            return currentPlaying.firstOrNull { it.id == UUID.fromString(id) }
        }
    }

    fun start() {
        MPEX.instance.server.pluginManager.registerEvents(this, MPEX.instance)
        currentPlaying.add(this)
        ready()
        countdown()
    }

    private fun ready() {
        players.forEachIndexed { index, player ->
            if (map.startPosition.size > index) player.teleport(map.startPosition[index])
            else player.teleport(map.startPosition[0])
            player.gameMode = GameMode.ADVENTURE
            player.inventory.clear()
            map.weapon.forEach { player.inventory.addItem(it.itemStack) }
        }
        map.specialFeature?.let { MPEX.instance.server.pluginManager.registerEvents(it, MPEX.instance) }
    }

    private fun countdown() {
        val moveLockHandler = object : Listener {
            @EventHandler
            fun onMove(e: PlayerMoveEvent) {
                if (players.contains(e.player)) e.isCancelled = true
            }
        }

        MPEX.instance.server.pluginManager.registerEvents(moveLockHandler, MPEX.instance)

        players.forEach {
            it.showTitle(
                Title.title(Component.text("READY").color(TextColor.color(0x00ffaa00)),
                    Component.text(""),
                    Title.Times.times(Duration.ofMillis(500L), Duration.ofSeconds(2L), Duration.ZERO))
            )
        }

        for (i in 1..3) {
            Bukkit.getScheduler().runTaskLater(MPEX.instance, Runnable {
                players.forEach {
                    it.showTitle(
                        Title.title(Component.text("READY").color(TextColor.color(0x00ffaa00)),
                            Component.text(4-i),
                            Title.Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ZERO))
                    )
                    it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
                }
            }, i*40L)
        }

        Bukkit.getScheduler().runTaskLater(MPEX.instance, Runnable {
            players.forEach {
                it.showTitle(
                    Title.title(Component.text("START!").color(TextColor.color(0x0055ff55)),
                        Component.text(""),
                        Title.Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ofSeconds(1L)))
                )
                it.playSound(it.location, Sound.EVENT_RAID_HORN, 100f, 1f)
            }

            HandlerList.unregisterAll(moveLockHandler)
        }, 160L)
    }

    private val deathPlayers = mutableListOf<Player>()

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        if (players.contains(e.player)) {
            deathPlayers.add(e.player)

            val leftPlayers = players.subtract(deathPlayers.toSet())
            if (leftPlayers.size == 1) {
                win(leftPlayers.first())
                stop()
            }
        }
    }

    private fun win(player: Player) {
        players.forEach {
            if (it == player) {
                it.showTitle(
                    Title.title(Component.text("WON!").color(TextColor.color(0x0055ff55)),
                        Component.text("Awesome!"),
                        Title.Times.times(Duration.ofMillis(250L), Duration.ofSeconds(2L), Duration.ofSeconds(1L)))
                )
                it.playSound(it.location, Sound.ITEM_TOTEM_USE, 100f, 1f)
            } else {
                it.showTitle(
                    Title.title(Component.text("LOSE..").color(TextColor.color(0x00ff5555)),
                        Component.text("Try better for next time.."),
                        Title.Times.times(Duration.ofMillis(250L), Duration.ofSeconds(2L), Duration.ofSeconds(1L)))
                )
                it.playSound(it.location, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 100f, 1f)
            }

            it.inventory.clear()
        }
    }

    fun stop() {
        HandlerList.unregisterAll(this)
        map.specialFeature?.let { HandlerList.unregisterAll(it) }
        currentPlaying.remove(this)
    }
}