package io.github.closeddev.mpex.events

import io.github.closeddev.mpex.pdc.WeaponDataType
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class ReloadWeaponEvent : Listener {

    companion object {
        val instance = ReloadWeaponEvent()
    }

    @EventHandler
    fun onReload(e: PlayerDropItemEvent) {
        if (e.itemDrop.itemStack.itemMeta.persistentDataContainer.has(Weapon.WEAPON_DATA)) {
            e.player.inventory.setItemInMainHand(e.itemDrop.itemStack.clone())
            val i = e.player.inventory.itemInMainHand
            val weapon = i.itemMeta.persistentDataContainer.get(Weapon.WEAPON_DATA, WeaponDataType())!!
            weapon.reload(i)

            e.itemDrop.remove()
        }
    }
}