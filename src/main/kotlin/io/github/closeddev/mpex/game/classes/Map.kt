package io.github.closeddev.mpex.game.classes

import io.github.closeddev.mpex.game.maps.RuinedVillage
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.Location
import org.bukkit.event.Listener

interface Map {
    companion object {
        val MAP_LIST = listOf(RuinedVillage())

        fun getMapInstance(id: String): Map? {
            return MAP_LIST.firstOrNull() { it.id == id }
        }
    }

    val name: String // 노르테유 익스프레스
    val id: String // NORTHEU_EXPRESS
    val startPosition: List<Location>
    val respawnPosition: List<Location>
    val weapon: List<Weapon>
    val specialFeature: Listener?
}