package io.github.closeddev.mpex.events

import io.github.closeddev.mpex.MPEX
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector

class WallJump : Listener {

    val DASH_POWER = 0.5
    val LIMIT_DASH = NamespacedKey(MPEX.instance, "limit_dash")

    @EventHandler
    fun onWalllJump(e: PlayerSwapHandItemsEvent) {
        e.isCancelled = true

        val player = e.player

        if (player.location.getBlockType(0.0, -0.1, 0.0) == Material.AIR) {
            if (!player.isSneaking) {
                if (!player.isSprinting) return
                if (player.persistentDataContainer.get(LIMIT_DASH, PersistentDataType.BOOLEAN)!!) return

                player.velocity = when {
                    player.location.getBlockType(1, 0, 0) != Material.AIR ->
                        Vector(-DASH_POWER, DASH_POWER, player.location.direction.z*DASH_POWER)
                    player.location.getBlockType(-1, 0, 0) != Material.AIR ->
                        Vector(DASH_POWER, DASH_POWER, player.location.direction.z*DASH_POWER)
                    player.location.getBlockType(0, 0, 1) != Material.AIR ->
                        Vector(player.location.direction.x*DASH_POWER, DASH_POWER, -DASH_POWER)
                    player.location.getBlockType(0, 0, -1) != Material.AIR ->
                        Vector(player.location.direction.x*DASH_POWER, DASH_POWER, DASH_POWER)
                    else -> return
                }

                player.persistentDataContainer.set(LIMIT_DASH, PersistentDataType.BOOLEAN, true)
            } else {

            }
        }
//                if (p.getLocation().subtract(1.0, 0.0, 0.0).getBlock().getType() != Material.AIR ||
//                    p.getLocation().subtract(-1.0, 0.0, 0.0).getBlock().getType() != Material.AIR ||
//                    p.getLocation().subtract(0.0, 0.0, 1.0).getBlock().getType() != Material.AIR ||
//                    p.getLocation().subtract(0.0, 0.0, -1.0).getBlock().getType() != Material.AIR) {
//                    Vector unitVector = new Vector(0.0, 0.6, 0.0);
//                    p.setVelocity(unitVector);
//                }
    }

    @EventHandler
    fun onGround(e: PlayerMoveEvent) {
        if (e.player.location.getBlockType(0.0, -0.1, 0.0) != Material.AIR) {
            e.player.persistentDataContainer.set(LIMIT_DASH, PersistentDataType.BOOLEAN, false)
        }
    }


    private fun Location.getBlockType(x: Int, y: Int, z: Int): Material {
        return this.add(x.toDouble(), y.toDouble(), z.toDouble()).block.type
    }

    private fun Location.getBlockType(x: Double, y: Double, z: Double): Material {
        return this.add(x, y, z).block.type
    }
}