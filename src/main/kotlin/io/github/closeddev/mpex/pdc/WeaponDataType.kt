package io.github.closeddev.mpex.pdc

import com.google.gson.Gson
import com.google.gson.JsonElement
import io.github.closeddev.mpex.weapons.Weapon
import org.bukkit.Material
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType

class WeaponDataType : PersistentDataType<String, Weapon> {
    override fun getPrimitiveType(): Class<String> {
        return String::class.java
    }

    override fun getComplexType(): Class<Weapon> {
        return Weapon::class.java
    }

    override fun fromPrimitive(p0: String, p1: PersistentDataAdapterContext): Weapon {

        val jsonObject = Gson().fromJson(p0, JsonElement::class.java).asJsonObject

        return object : Weapon {
            override val name: String = jsonObject.get("name").asString
            override val fireLoop: Int = jsonObject.get("fireLoop").asInt
            override val fireWait: Long = jsonObject.get("fireWait").asLong
            override val damage: Float = jsonObject.get("damage").asFloat
            override val maxAmo: Int = jsonObject.get("maxAmo").asInt
            override val reloadLength: Long = jsonObject.get("reloadLength").asLong
            override val tacticalReloadLength: Long = jsonObject.get("tacticalReloadLength").asLong
            override val material: Material = Material.getMaterial(jsonObject.get("material").asString) ?: Material.AIR
            override val fireType: Weapon.FireType = Weapon.FireType.valueOf(jsonObject.get("fireType").asString)
            override val fireCooldown: Long = jsonObject.get("fireCooldown").asLong
        }
    }

    override fun toPrimitive(p0: Weapon, p1: PersistentDataAdapterContext): String {
        return Gson().toJson(p0)
    }
}