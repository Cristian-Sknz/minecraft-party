package me.sknz.minecraft.party.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.inventory.applyMetaData
import me.sknz.minecraft.party.configurations.WorkshopConfiguration
import me.sknz.minecraft.party.configurations.WorkshopPlayerConfiguration
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.regex.Pattern

@OptIn(ExperimentalPluginFeature::class)
class WorkshopPlayerPositionItem(val configuration: WorkshopPlayerConfiguration,
                                 private val state: HashMap<UUID, Int?>) :
    ItemHandler(Material.SKULL_ITEM, "Posição do Jogador", ALL_CLICK) {


    override fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope) {
        val spawns = configuration.slots.size
        val counter = item.getCounter()

        if (scope.isLeftClick()) {
            when {
                spawns == 0 -> item.setNewSelection(player)
                counter == null -> item.setCounter(player, 1)
                counter < spawns -> item.setCounter(player, counter + 1)
                counter == 12 -> item.setCounter(player, 1)
                counter == spawns -> item.setNewSelection(player)
            }
            return
        }

        val location = player.location

        if (counter == null) {
            val slot = configuration.slots.size + 1
            configuration.add(location)

            state[player.uniqueId] = slot
            item.amount = slot
            item.setCounter(player, slot)
            item.applyMetaData { lore[2] = "§3(${location.x}, ${location.y}, ${location.z})" }
        } else {
            state[player.uniqueId] = counter
            configuration[counter - 1].spawn = location
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

    private fun ItemStack.setNewSelection(player: Player) {
        amount = 1
        state[player.uniqueId] = null
        applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$displayName §3(Novo Spawn)"
        }
    }

    private fun ItemStack.setCounter(player: Player, value: Int) {
        amount = value
        state[player.uniqueId] = value - 1
        applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$displayName §3($value)"
            lore = mutableListOf("§7Utilize este item para setar", "§7o spawns de jogadores na sua posição", "§3(${configuration[value -1].spawn.run { "$x, $y, $z" }})")
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
