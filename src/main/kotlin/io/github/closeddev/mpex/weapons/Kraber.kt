package io.github.closeddev.mpex.weapons

import org.bukkit.Material

class Kraber : Weapon() {
    override val name: String = "Kraber .50-Cal Sniper"
    override val fireLoop: Int = 1
    override val fireWait: Long = 0L
    override val damage: Float = 20.0F
    override val maxAmo: Int = 4
    override val reloadLength: Long = 430L
    override val tacticalReloadLength: Long = 320L
    override val material: Material = Material.SPYGLASS
    override val fireType: FireType = FireType.SINGLE_FIRE
    override val fireCooldown: Long = 240L
}