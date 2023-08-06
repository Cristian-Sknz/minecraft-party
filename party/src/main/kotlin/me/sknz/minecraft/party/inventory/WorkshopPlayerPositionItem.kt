package me.sknz.minecraft.party.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.inventory.applyMetaData
import me.sknz.minecraft.party.configurations.WorkshopConfiguration
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.regex.Pattern
import javax.swing.plaf.basic.BasicComboBoxUI

@OptIn(ExperimentalPluginFeature::class)
class WorkshopPlayerPositionItem :
    ItemHandler(Material.SKULL_ITEM, "Posição do Jogador", SELECT_AND_CLICK) {

    var spawns = 0

    override fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope) {
        if (scope.isLeftClick()) {
            val counter = item.getCounter()
            //val spawns = configuration.spawns.size

            when {
                spawns == 0 -> item.setNewSelection()
                counter == null -> item.setCounter(1)
                counter < spawns -> item.setCounter(counter + 1)
                counter == 12 -> item.setCounter(1)
                counter == spawns -> item.setNewSelection()
            }
            return
        }

        val counter = item.getCounter()
        val block = scope.block!!

        if (counter == null) {
            spawns++

            if (spawns == 12) {
                item.setCounter(12)
                item.applyMetaData { lore[2] = "§3(${block.x}, ${block.y}, ${block.z})" }
            }
        } else {
            item.applyMetaData { lore[2] = "§3(${block.x}, ${block.y}, ${block.z})" }
        }

        player.updateInventory()
        player.sendMessage("§e[Configuração] §aVocê setou o spawn do player em §e(${block.x}, ${block.y}, ${block.z})")
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
        applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$displayName §3(Novo Spawn)"
        }
    }

    private fun ItemStack.setCounter(value: Int) {
        applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$displayName §3($value)"
        }
    }

    override fun getItemCopy(): ItemStack {
        return ItemStack(Material.SKULL_ITEM).apply {
            val meta = Bukkit.getItemFactory().getItemMeta(this.type)
            meta.displayName = "§6${this@WorkshopPlayerPositionItem.name} §3(Novo Spawn)"
            meta.lore = mutableListOf("§7Utilize este item para setar", "§7o spawns de jogadores", "§3(Sem valor definido)")
            this.itemMeta = meta
        }
    }
}
