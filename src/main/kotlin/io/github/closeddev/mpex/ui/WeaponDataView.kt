package io.github.closeddev.mpex.ui

import io.github.closeddev.mpex.pdc.WeaponDataType
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import kotlin.math.roundToInt

class WeaponDataView : Runnable {
    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.inventory.itemInMainHand.isEmpty) { clearExp(player); return }

            val container = player.inventory.itemInMainHand.itemMeta.persistentDataContainer
            val weapon = container.get(Weapon.WEAPON_DATA, WeaponDataType()) ?: continue

            if (container.has(Weapon.WEAPON_RELOAD)) {
                val cooldown = container.get(Weapon.WEAPON_RELOAD, PersistentDataType.FLOAT)
                if (cooldown == null) { clearExp(player); return }

                val maxCoolDown = (if (container.get(Weapon.WEAPON_IS_TACTICAL, PersistentDataType.BOOLEAN) == true) weapon.tacticalReloadLength.toFloat() else weapon.reloadLength.toFloat())*0.01F

                player.exp = (maxCoolDown - cooldown) / maxCoolDown
                player.level = (((maxCoolDown - cooldown) / maxCoolDown) * weapon.maxAmo.toFloat()).roundToInt()
            } else {
                val leftAmo = container.get(Weapon.WEAPON_AMO, PersistentDataType.INTEGER)
                if (leftAmo == null) { clearExp(player); return }

                player.exp = leftAmo.toFloat() / weapon.maxAmo.toFloat()
                player.level = leftAmo
            }
        }
    }

    private fun clearExp(player: Player) {
        player.exp = 0f
        player.level = 0
    }
}