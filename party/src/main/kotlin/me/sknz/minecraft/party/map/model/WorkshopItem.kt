package me.sknz.minecraft.party.map.model

import com.fasterxml.jackson.module.kotlin.readValue
import me.sknz.minecraft.json
import me.sknz.minecraft.party.instance
import org.bukkit.Material

data class WorkshopItem(
    val name: String,
    val material: Material,
    val materials: List<Material>
) {
    companion object {
        fun getItems(): List<WorkshopItem> {
            val stream = instance.getResource("workshop_itens.json")
            val node = json.readTree(stream)

            return json.readValue<Array<WorkshopItem>>(node.get("workshop_items").traverse()).toList()
        }

    }
}