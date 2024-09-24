package io.github.closeddev.mpex.game.games

import io.github.closeddev.mpex.MPEX
import io.github.closeddev.mpex.events.ProjectileEvents
import io.github.closeddev.mpex.game.classes.Game
import io.github.closeddev.mpex.game.classes.Map
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask
import java.time.Duration

class DeathMatchGame(
    override val map: Map,
    val redPlayers: List<Player>,
    val bluePlayers: List<Player>
) : Game() {
    override val players: List<Player> = redPlayers + bluePlayers

    override val isAvailable: Boolean
        get() {
            return redPlayers.isNotEmpty() && bluePlayers.isNotEmpty()
        }


    private val kills2win = players.size*5
    private val kills = mutableMapOf<Player, Int>()
    private val teamSumKills: Pair<Int, Int>
        get() {
            return kills.entries.groupingBy {
                if (redPlayers.contains(it.key)) "red" else "blue"
            }.fold(0) { acc, entry -> acc + entry.value}.let { map ->
                (map["red"] ?: 0) to (map["blue"] ?: 0)
            }
        }

    private val redKills: Int
        get() = teamSumKills.first
    private val blueKills: Int
        get() = teamSumKills.second

    override val winConditionListener: Listener = object : Listener {

        @EventHandler
        fun onKill(e: PlayerDeathEvent) {
            println("${e.player.name}, ${e.player.killer?.name}")

            if (!players.contains(e.player) || !players.contains(e.player.killer)) return

            val attacker = e.player.killer!!
            val victim = e.player

            if (!(attacker in redPlayers && victim in bluePlayers) && !(victim in redPlayers && attacker in bluePlayers)) return

            kills[attacker] = (kills[attacker] ?: 0) + 1

            if (redKills >= kills2win) win(redPlayers)
            else if (blueKills >= kills2win) win(bluePlayers)
        }

        @EventHandler
        fun onRespawn(e: PlayerRespawnEvent) {
            val enemyPlayers =
                if (redPlayers.contains(e.player)) bluePlayers
                else if (bluePlayers.contains(e.player)) redPlayers
                else return

            val respawnLocation = map.respawnPosition.maxByOrNull { respawn ->
                enemyPlayers.minOfOrNull { enemy ->
                    enemy.location.distance(respawn)
                } ?: Double.MAX_VALUE
            } ?: map.startPosition.random()

            e.respawnLocation = respawnLocation

            e.player.persistentDataContainer.set(ProjectileEvents.IS_IMMUNE, PersistentDataType.BOOLEAN, true)

            Bukkit.getScheduler().runTaskLater(MPEX.instance, Runnable {
                e.player.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 119, 255, true))
            }, 1L)

            Bukkit.getScheduler().runTaskLaterAsynchronously(MPEX.instance, Runnable {
                e.player.persistentDataContainer.remove(ProjectileEvents.IS_IMMUNE)
            }, 120L)
        }
    }

    private lateinit var showKillsTask: BukkitTask

    override fun onStart() {
        showKillsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MPEX.instance, Runnable {
            redPlayers.forEach {
                it.showTitle(Title.title(Component.text(""),
                    Component.text(redKills).color(TextColor.color(0x0055ff55))
                        .append(Component.text(" : ").color(TextColor.color(0x00aaaaaa)))
                        .append(Component.text(blueKills).color(TextColor.color(0x00ff5555))),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ofSeconds(1L))))
            }

            bluePlayers.forEach {
                it.showTitle(Title.title(Component.text(""),
                    Component.text(blueKills).color(TextColor.color(0x0055ff55))
                        .append(Component.text(" : ").color(TextColor.color(0x00aaaaaa)))
                        .append(Component.text(redKills).color(TextColor.color(0x00ff5555))),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ofSeconds(1L))))
            }
        }, 0L, 20L)
    }

    override fun onStop() {
        showKillsTask.cancel()
    }
}