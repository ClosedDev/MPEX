package io.github.closeddev.mpex.game.maps

import io.github.closeddev.mpex.game.classes.Map
import io.github.closeddev.mpex.weapons.Carbine
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Listener

class RuinedVillage : Map {
    override val name: String = "Ruined Village"
    override val id: String = "VILLAGE_RUINED"
    override val startPosition: List<Location> = listOf(
        Location(Bukkit.getWorld("world"), -56.5, -12.0, 29.5, -135f, 0f),
        Location(Bukkit.getWorld("world"), -12.5, -12.0, -14.5, 45f, 0f)
    )
    override val weapon: List<Weapon> = listOf(Carbine())
    override val specialFeature: Listener? = null
}