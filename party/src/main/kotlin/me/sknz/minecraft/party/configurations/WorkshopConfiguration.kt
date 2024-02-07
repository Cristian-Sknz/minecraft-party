package me.sknz.minecraft.party.configurations

import com.fasterxml.jackson.annotation.JsonIgnore
import me.sknz.minecraft.party.instance
import me.sknz.minecraft.json
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File
import java.io.FileOutputStream

class WorkshopConfiguration: GameConfiguration {

    override var location: Location = Bukkit.getWorlds()[0].spawnLocation
    var spawns: MutableList<Location?> = mutableListOf()
    var frame: List<Location> = emptyList()
    var blocks: MutableList<Location> = mutableListOf()
    var villager: Location = Bukkit.getWorlds()[0].spawnLocation

    @JsonIgnore
    var file: String? = null

    fun setFrame(firstPoint: Location, secondPoint: Location) {
        frame = mutableListOf(firstPoint, secondPoint)
    }

    fun setSpawn(slot: Int, location: Location) {
        if (slot >= 12) throw RuntimeException("Você só pode setar 12 slots para o minigame")
        this.spawns[slot] = location
    }

    override fun save() {
        val output = FileOutputStream(File(file!!))
        json.writeValue(output, this)
    }

    companion object {
        fun load(): WorkshopConfiguration {
            val file = File("${instance.dataFolder.absolutePath}/workshop.json")
            if (!file.exists()) {
                instance.dataFolder.mkdirs()
                file.createNewFile()

                return WorkshopConfiguration().apply {
                    this.file = file.absolutePath
                    json.writeValue(FileOutputStream(file), this)
                }
            }

            return json.readValue(file, WorkshopConfiguration::class.java).apply {
                this.file = file.absolutePath
            }
        }
    }
}
