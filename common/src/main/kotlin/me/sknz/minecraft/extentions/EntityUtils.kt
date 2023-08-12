package me.sknz.minecraft.extentions

import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

/**
 * ## [EntityUtils]
 *
 * Classe de utilidades para Entidades do Bukkit
 */
object EntityUtils {

    /**
     * Utiliza NMS para freezar uma entidade, isso funciona apenas para entidades
     * e não para players.
     */
    fun Entity.freeze() {
        if (this !is CraftEntity) return
        val nbcEn = this.handle

        val compound = NBTTagCompound()
        nbcEn.c(compound)
        compound.setByte("NoAI", 1.toByte())
        nbcEn.f(compound)
    }

    inline fun <reified E : Entity>World.spawnEntity(loc: Location): E {
        return this.spawnEntity(loc, EntityType.entries.find { E::class.java == it.entityClass }) as E
    }
}