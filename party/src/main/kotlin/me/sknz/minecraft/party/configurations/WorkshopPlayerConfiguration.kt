package me.sknz.minecraft.party.configurations

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector

class WorkshopPlayerConfiguration: GameConfiguration {

    override var location: Location = Bukkit.getWorlds()[0].spawnLocation
    var slots: MutableList<SlotConfiguration> = mutableListOf()

    operator fun get(index: Int): SlotConfiguration {
        return slots[index]
    }

    fun add(location: Location): SlotConfiguration {
        return SlotConfiguration(location, emptyList(), mutableListOf(), null).apply(slots::add)
    }

    fun remove(index: Int) {
        slots.removeAt(index)
    }

    data class SlotConfiguration(
        var spawn: Location,
        var frame: List<Location>,
        var blocks: MutableList<Vector>,
        var villager: Location?
    )

    override fun save() {
        TODO("Not yet implemented")
    }
}