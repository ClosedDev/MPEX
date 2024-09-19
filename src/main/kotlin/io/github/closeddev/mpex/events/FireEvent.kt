package io.github.closeddev.mpex.events

import io.github.closeddev.mpex.pdc.WeaponDataType
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot

class FireEvent : Listener {
    @EventHandler
    fun onFire(e: PlayerInteractEvent) {
        val player = e.player

        if (player.inventory.itemInMainHand.isEmpty) return
        if (e.hand != EquipmentSlot.HAND) return

        val weapon = player.inventory.itemInMainHand.itemMeta.persistentDataContainer.get(Weapon.WEAPON_DATA, WeaponDataType()) ?: return

        if (e.action.name.contains("RIGHT") && weapon.fireType == Weapon.FireType.MULTIPLE_FIRE) {
            weapon.fire(player, player.inventory.itemInMainHand)
        } else if (e.action.name.contains("LEFT") && weapon.fireType == Weapon.FireType.SINGLE_FIRE) {
            weapon.fire(player, player.inventory.itemInMainHand)
        } else return

        e.isCancelled = true
    }

    @EventHandler
    fun onFireUseF(e: PlayerSwapHandItemsEvent) {
        val player = e.player

        if (player.inventory.itemInMainHand.isEmpty) return

        val weapon = player.inventory.itemInMainHand.itemMeta.persistentDataContainer.get(Weapon.WEAPON_DATA, WeaponDataType()) ?: return
        if (weapon.fireType == Weapon.FireType.SINGLE_FIRE) {
            weapon.fire(player, player.inventory.itemInMainHand)
        } else return

        e.isCancelled = true
    }
}