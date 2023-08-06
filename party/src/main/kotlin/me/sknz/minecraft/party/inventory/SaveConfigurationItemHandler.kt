package me.sknz.minecraft.party.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.party.configurations.GameConfiguration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@OptIn(ExperimentalPluginFeature::class)
class SaveConfigurationItemHandler(val configuration: GameConfiguration)
    : ItemHandler(Material.BED, "Concluir as Configurações", RIGHT_CLICK) {
    override fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope) {
        configuration.save()
        player.sendMessage("§aSuas configurações foram salvas com sucesso.")
    }

    override fun getItemCopy(): ItemStack {
        return ItemStack(this.type).apply {
            val meta = Bukkit.getItemFactory().getItemMeta(this.type)
            meta.displayName = "§6${this@SaveConfigurationItemHandler.name}"
            meta.lore = listOf("§7Utilize este item para concluir", "§7a configuração desde modo")
            this.itemMeta = meta
        }
    }
}