package io.github.closeddev.mpex.events

import io.github.closeddev.mpex.MPEX
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerPickupArrowEvent
import org.bukkit.persistence.PersistentDataType


class ProjectileEvents : Listener {

    companion object {
        val instance = ProjectileEvents()

        val IS_IMMUNE = NamespacedKey(MPEX.instance, "PROJECTILE_EVENTS_IS_IMMUNE")
    }

    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (e.damager.type == EntityType.ARROW) {
            val projectile = e.damager as Projectile
            val attacker = projectile.shooter as Player
            val container = projectile.persistentDataContainer

            if (container.has(Weapon.WEAPON_DAMAGE)) { e.damage = container.get(Weapon.WEAPON_DAMAGE, PersistentDataType.FLOAT)!!.toDouble() }
            else return // if not weapon

            val victim = e.entity as LivingEntity

            if (victim.persistentDataContainer.has(IS_IMMUNE)) {
                e.isCancelled = true

                attacker.playSound(attacker.location, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 0.6f, 1f)
                victim.world.playSound(victim.location, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1f, 1f)

                victim.world.spawnParticle(Particle.ENCHANT, victim.location, 20, 0.25, 1.0, 0.25, 0.25)
            } else {
                if ((victim.health - e.damage) <= 0) {
                    e.isCancelled = false
                } else {
                    victim.health -= e.damage
                    e.isCancelled = true
                }

                attacker.playSound(attacker.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.6f, 1f)
                victim.world.playSound(victim.location, Sound.ENTITY_PLAYER_HURT, 1f, 1f)

                victim.world.spawnParticle(Particle.CRIT, victim.location, 20, 0.25, 1.0, 0.25, 0.25)
            }
        } else {
            e.damage = 1.0
            (e.entity as LivingEntity).noDamageTicks = 0
        }
    }

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        val pr = e.entity
        if (pr.type == EntityType.ARROW) {
            if (!pr.persistentDataContainer.has(Weapon.WEAPON_DAMAGE)) return
            if (e.hitEntity == null && e.hitBlock!!.type != Material.BARRIER) {
                Bukkit.getScheduler().runTaskLater(MPEX.instance, Runnable {
                    pr.remove()
                }, 80L)
            } else {
                pr.remove()
            }
        }
    }

    @EventHandler
    fun onPickupArrow(e: PlayerPickupArrowEvent) {
        val pr: Projectile = e.arrow
        if (pr.persistentDataContainer.has(Weapon.WEAPON_DAMAGE)) {
            e.isCancelled = true
        }
    }
}