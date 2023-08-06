package me.sknz.minecraft.party.configurations

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector

class WorkshopConfiguration: GameConfiguration {

    override var location: Location = Bukkit.getWorlds()[0].spawnLocation
    lateinit var spawns: MutableList<Location?>
    var frame: List<Vector> = emptyList()
    lateinit var blocks: MutableList<Location>

    fun setFrame(x: Vector, y: Vector) {
        frame = mutableListOf(x, y)
    }

    fun setSpawn(slot: Int, location: Location) {
        if (slot >= 12) throw RuntimeException("Você só pode setar 12 slots para o minigame")
        this.spawns[slot] = location
    }
}
