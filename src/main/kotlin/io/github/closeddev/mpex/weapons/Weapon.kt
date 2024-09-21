package io.github.closeddev.mpex.weapons

import io.github.closeddev.mpex.MPEX
import io.github.closeddev.mpex.pdc.WeaponDataType
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

interface Weapon {

    companion object {
        val WEAPON_DATA = NamespacedKey(MPEX.instance, "weapon_data")

        val WEAPON_NAME = NamespacedKey(MPEX.instance, "weapon_name")
        val WEAPON_DAMAGE = NamespacedKey(MPEX.instance, "weapon_damage")
        val WEAPON_AMO = NamespacedKey(MPEX.instance, "weapon_amo")
        val WEAPON_COOLDOWN = NamespacedKey(MPEX.instance, "weapon_cooldown")
        val WEAPON_RELOAD = NamespacedKey(MPEX.instance, "weapon_reload")
        val WEAPON_IS_TACTICAL = NamespacedKey(MPEX.instance, "weapon_is_tactical")
    }

    enum class FireType {
        SINGLE_FIRE,
        MULTIPLE_FIRE
    }

    val name: String
    val fireLoop: Int
    val fireWait: Long
    val damage: Float
    val maxAmo: Int
    val reloadLength: Long
    val tacticalReloadLength: Long
    val material: Material
    val fireType: FireType
    val fireCooldown: Long

    val itemStack: ItemStack
        get() = ItemStack(material).apply {
            val meta = this@apply.itemMeta
            meta.persistentDataContainer.set(WEAPON_DATA, WeaponDataType(), this@Weapon)
            meta.persistentDataContainer.set(WEAPON_AMO, PersistentDataType.INTEGER, maxAmo)
            this@apply.itemMeta = meta
        }

    fun fire(player: Player, itemStack: ItemStack) {
        val meta = itemStack.itemMeta

        if (meta.persistentDataContainer.get(WEAPON_COOLDOWN, PersistentDataType.BOOLEAN) == true) return

        for (i in 0..<fireLoop) {
            Bukkit.getScheduler().runTaskLater(MPEX.instance, Runnable {
                val leftAmo = meta.persistentDataContainer.get(WEAPON_AMO, PersistentDataType.INTEGER)!!
                if (leftAmo == 0) {
                    player.playSound(player.location, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.5f, 1.7f)
                    return@Runnable
                }
                meta.persistentDataContainer.set(WEAPON_AMO, PersistentDataType.INTEGER, leftAmo - 1)
                itemStack.itemMeta = meta

                val projectile = player.launchProjectile(org.bukkit.entity.Arrow::class.java)
                projectile.shooter = player
                projectile.velocity = player.location.direction.multiply(10)
                projectile.persistentDataContainer.set(WEAPON_NAME, PersistentDataType.STRING, name)
                projectile.persistentDataContainer.set(WEAPON_DAMAGE, PersistentDataType.FLOAT, damage)

                player.world.playSound(player.location, Sound.ENTITY_ARROW_SHOOT, 0.5f, 1.0f)
            }, i*fireWait)
        }

        val amo = meta.persistentDataContainer.get(WEAPON_AMO, PersistentDataType.INTEGER)!!
        if (amo == 0) { return }

        meta.persistentDataContainer.set(WEAPON_COOLDOWN, PersistentDataType.BOOLEAN, true)
        itemStack.itemMeta = meta
        Bukkit.getScheduler().runTaskLater(MPEX.instance, Runnable {
            meta.persistentDataContainer.set(WEAPON_COOLDOWN, PersistentDataType.BOOLEAN, false)
            itemStack.itemMeta = meta
            if (fireType == FireType.SINGLE_FIRE) player.playSound(player.location, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 0.5f, 1.7f)
        }, fireCooldown)
    }

    fun reload(itemStack: ItemStack) {
        val meta = itemStack.itemMeta

        val leftAmo = meta.persistentDataContainer.get(WEAPON_AMO, PersistentDataType.INTEGER)!!
        if (leftAmo == maxAmo) return

        meta.persistentDataContainer.set(WEAPON_COOLDOWN, PersistentDataType.BOOLEAN, true)
        meta.persistentDataContainer.set(WEAPON_IS_TACTICAL, PersistentDataType.BOOLEAN, leftAmo > 0)

        val reloadTime =
            if (meta.persistentDataContainer.has(WEAPON_RELOAD)) { (meta.persistentDataContainer.get(WEAPON_RELOAD, PersistentDataType.FLOAT)!!*100.0F).toLong() }
            else { if (leftAmo > 0) tacticalReloadLength else reloadLength }

        for (i in 0..<reloadTime) {
            Bukkit.getScheduler().runTaskLater(MPEX.instance, Runnable {
                meta.persistentDataContainer.set(WEAPON_RELOAD, PersistentDataType.FLOAT, (reloadTime-i)*0.01f)
                itemStack.itemMeta = meta
            }, i)
        }

        Bukkit.getScheduler().runTaskLater(MPEX.instance, Runnable {
            meta.persistentDataContainer.remove(WEAPON_RELOAD)
            meta.persistentDataContainer.remove(WEAPON_IS_TACTICAL)
            meta.persistentDataContainer.set(WEAPON_COOLDOWN, PersistentDataType.BOOLEAN, false)
            meta.persistentDataContainer.set(WEAPON_AMO, PersistentDataType.INTEGER, maxAmo)
            itemStack.itemMeta = meta
        }, reloadTime)
    }
}