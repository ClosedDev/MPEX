package io.github.closeddev.mpex.game.games

import io.github.closeddev.mpex.game.classes.Game
import io.github.closeddev.mpex.game.classes.Map
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class BattleRoyaleGame(
    override val map: Map,
    override val players: List<Player>
) : Game() {
    override val isAvailable: Boolean
        get() {
            return players.distinct().size >= 2
        }

    override val winConditionListener: Listener = object : Listener {
        private val deathPlayers = mutableListOf<Player>()

        @EventHandler
        fun onDeath(e: PlayerDeathEvent) {
            if (players.contains(e.player)) {
                deathPlayers.add(e.player)

                val leftPlayers = players.subtract(deathPlayers.toSet())
                if (leftPlayers.size == 1) {
                    win(leftPlayers.toList())
                }
            }
        }
    }
}