package io.github.closeddev.mpex.events

import io.github.closeddev.mpex.pdc.WeaponDataType
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class FireEvent : Listener {
    @EventHandler
    fun onFire(e: PlayerInteractEvent) {
        if (e.player.inventory.itemInMainHand.isEmpty) return
        if (e.hand != EquipmentSlot.HAND) return

        val weapon = e.player.inventory.itemInMainHand.itemMeta.persistentDataContainer.get(Weapon.WEAPON_DATA, WeaponDataType()) ?: return

        if (e.action.name.contains("RIGHT") && weapon.fireType == Weapon.FireType.MULTIPLE_FIRE) {
            weapon.fire(e.player, e.player.inventory.itemInMainHand)
        } else if (e.action.name.contains("LEFT") && weapon.fireType == Weapon.FireType.SINGLE_FIRE) {
            weapon.fire(e.player, e.player.inventory.itemInMainHand)
        } else return

        e.isCancelled = true
    }
}