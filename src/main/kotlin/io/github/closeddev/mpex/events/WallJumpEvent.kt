package io.github.closeddev.mpex.events

import io.github.closeddev.mpex.MPEX
import io.github.closeddev.mpex.pdc.WeaponDataType
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector

class WallJumpEvent : Listener {

    private val dashPower = 0.5
    private val isDashAllowed = NamespacedKey(MPEX.instance, "limit_dash")

    @EventHandler
    fun onWallJump(e: PlayerSwapHandItemsEvent) {
        if (e.player.inventory.itemInMainHand.isEmpty) return
        if (e.player.inventory.itemInMainHand.itemMeta.persistentDataContainer
            .get(Weapon.WEAPON_DATA, WeaponDataType())?.fireType == Weapon.FireType.SINGLE_FIRE) return

        e.isCancelled = true

        val player = e.player

        if (player.location.getBlockType(0.0, -0.1, 0.0) == Material.AIR) {
            if (!player.isSneaking) {
//                if (!player.isSprinting) return
                if (player.persistentDataContainer.get(isDashAllowed, PersistentDataType.BOOLEAN)!!) return

                player.velocity = when {
                    player.location.getBlockType(1, 0, 0) != Material.AIR ->
                        Vector(-dashPower, dashPower, player.location.direction.z*dashPower)
                    player.location.getBlockType(-1, 0, 0) != Material.AIR ->
                        Vector(dashPower, dashPower, player.location.direction.z*dashPower)
                    player.location.getBlockType(0, 0, 1) != Material.AIR ->
                        Vector(player.location.direction.x*dashPower, dashPower, -dashPower)
                    player.location.getBlockType(0, 0, -1) != Material.AIR ->
                        Vector(player.location.direction.x*dashPower, dashPower, dashPower)
                    else -> return
                }

                player.persistentDataContainer.set(isDashAllowed, PersistentDataType.BOOLEAN, true)
            }
        }
    }

    @EventHandler
    fun onGround(e: PlayerMoveEvent) {
        if (e.player.location.getBlockType(0.0, -0.1, 0.0) != Material.AIR) {
            e.player.persistentDataContainer.set(isDashAllowed, PersistentDataType.BOOLEAN, false)
        }
    }

    private fun Location.getBlockType(x: Int, y: Int, z: Int): Material {
        return this.add(x.toDouble(), y.toDouble(), z.toDouble()).block.type
    }

    private fun Location.getBlockType(x: Double, y: Double, z: Double): Material {
        return this.add(x, y, z).block.type
    }
}