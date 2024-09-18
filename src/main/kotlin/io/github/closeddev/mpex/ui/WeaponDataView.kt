package io.github.closeddev.mpex.ui

import io.github.closeddev.mpex.pdc.WeaponDataType
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.Bukkit
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.roundToInt

class WeaponDataView : Runnable {
    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.inventory.itemInMainHand.isEmpty) return

            val container = player.inventory.itemInMainHand.itemMeta.persistentDataContainer
            val weapon = container.get(Weapon.WEAPON_DATA, WeaponDataType()) ?: continue

            if (container.has(Weapon.WEAPON_RELOAD)) {
                val cooldown = container.get(Weapon.WEAPON_RELOAD, PersistentDataType.FLOAT) ?: return
                val maxcool = (if (container.get(Weapon.WEAPON_ISTACTICAL, PersistentDataType.BOOLEAN) == true) weapon.tacticalReloadLength.toFloat() else weapon.reloadLength.toFloat())*0.01F
                player.exp = (maxcool - cooldown) / maxcool
                player.level = (((maxcool - cooldown) / maxcool) * weapon.maxAmo.toFloat()).roundToInt()
            } else {
                val leftAmo = container.get(Weapon.WEAPON_AMO, PersistentDataType.INTEGER) ?: return
                player.exp = leftAmo.toFloat() / weapon.maxAmo.toFloat()
                player.level = leftAmo
            }
        }
    }
}