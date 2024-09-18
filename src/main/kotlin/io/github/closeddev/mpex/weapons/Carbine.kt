package io.github.closeddev.mpex.weapons

import org.bukkit.Material

class Carbine : Weapon() {
    override val name: String = "R-301 Carbine"
    override val fireLoop: Int = 3
    override val fireWait: Long = 7L
    override val damage: Float = 1.3F
    override val maxAmo: Int = 31
    override val reloadLength: Long = 288L
    override val tacticalReloadLength: Long = 216L
    override val material: Material = Material.NETHERITE_SWORD
    override val fireType: FireType = FireType.MULTIPLE_FIRE
    override val fireCooldown: Long = 19L
}