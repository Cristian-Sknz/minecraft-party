package me.sknz.minecraft.party.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.inventory.applyMetaData
import me.sknz.minecraft.party.configurations.WorkshopConfiguration
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.regex.Pattern

@OptIn(ExperimentalPluginFeature::class)
class WorkshopPlayerPositionItem(val configuration: WorkshopConfiguration) :
    ItemHandler(Material.SKULL_ITEM, "Posição do Jogador", ALL_CLICK) {


    override fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope) {
        val spawns = configuration.spawns.size
        val counter = item.getCounter()

        if (scope.isLeftClick()) {
            when {
                spawns == 0 -> item.setNewSelection()
                counter == null -> item.setCounter(1)
                counter < spawns -> item.setCounter(counter + 1)
                counter == 12 -> item.setCounter(1)
                counter == spawns -> item.setNewSelection()
            }
            return
        }

        val location = player.location

        if (counter == null) {
            configuration.spawns.add(location)
            item.amount = configuration.spawns.size
            item.setCounter(configuration.spawns.size)
            item.applyMetaData { lore[2] = "§3(${location.x}, ${location.y}, ${location.z})" }
        } else {
            configuration.setSpawn(counter - 1, location)
            item.amount = counter
            item.applyMetaData { lore[2] = "§3(${location.x}, ${location.y}, ${location.z})" }
        }

        player.updateInventory()
        player.sendMessage("§e[Configuração] §aVocê setou o spawn do player em §e(${location.x}, ${location.y}, ${location.z})")
    }

    private fun ItemStack.getCounter(): Int? {
        val text = ChatColor.stripColor(this.itemMeta.displayName)
        val pattern = Pattern.compile("\\((\\d+)\\)")
        val matcher = pattern.matcher(text)

        if (matcher.find()) {
            return matcher.group(1).toInt()
        }
        return null
    }

    private fun ItemStack.setNewSelection() {
        amount = 1
        applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$displayName §3(Novo Spawn)"
        }
    }

    private fun ItemStack.setCounter(value: Int) {
        amount = value
        applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$displayName §3($value)"
        }
    }

    override fun getItemCopy(): ItemStack {
        return ItemStack(Material.SKULL_ITEM).apply {
            val meta = Bukkit.getItemFactory().getItemMeta(this.type)
            meta.displayName = "§6${this@WorkshopPlayerPositionItem.name} §3(Novo Spawn)"
            meta.lore = mutableListOf("§7Utilize este item para setar", "§7o spawns de jogadores na sua posição", "§3(Sem valor definido)")
            this.itemMeta = meta
        }
    }
}
