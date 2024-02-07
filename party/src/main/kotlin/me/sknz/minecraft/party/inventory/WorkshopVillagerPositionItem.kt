package me.sknz.minecraft.party.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.inventory.applyMetaData
import me.sknz.minecraft.party.configurations.WorkshopConfiguration
import me.sknz.minecraft.party.configurations.WorkshopPlayerConfiguration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.inventory.ItemStack
import java.util.*

@OptIn(ExperimentalPluginFeature::class)
class WorkshopVillagerPositionItem(val configuration: WorkshopPlayerConfiguration, private val state: Map<UUID, Int?>) :
    ItemHandler(Material.MONSTER_EGG, "Posição do Aldeão de Troca", EnumSet.of(Action.RIGHT_CLICK_BLOCK)) {

    override fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope) {
        if (state[player.uniqueId] == null) {
            player.sendMessage("§cVocê precisa selecionar um spawn para configurar o villager.")
            return
        }

        val block = scope.block!!
        item.applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$name §3(${block.x}, ${block.y}, ${block.z})"
        }

        configuration[state[player.uniqueId]!!].villager = block.location.apply { x += 0.5F; z += 0.5F; y++ }

        player.updateInventory()
        player.sendMessage("§e[Configuração] §aVocê setou o spawn do villager em §e(${block.x}, ${block.y}, ${block.z})")
    }

    override fun getItemCopy(): ItemStack {
        return ItemStack(this.type).apply {
            val meta = Bukkit.getItemFactory().getItemMeta(this.type)
            meta.displayName = "§6${this@WorkshopVillagerPositionItem.name}"
            meta.lore = listOf("§7Utilize este item para setar", "§7a posição do aldeão de trocas")
            this.itemMeta = meta
        }
    }
}
