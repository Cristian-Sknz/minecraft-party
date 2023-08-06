package me.sknz.minecraft.party.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.inventory.applyMetaData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

@OptIn(ExperimentalPluginFeature::class)
class WorkshopBlockItemHandler: ItemHandler(Material.NAME_TAG, "Blocos de Interação", SELECT_AND_CLICK) {

    private val selection: HashMap<UUID, Block> = hashMapOf()

    override fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope) {
        if (scope.isLeftClick()) {
            item.applyMetaData {
                displayName = displayName.substringBefore(" §3(")
                displayName = "$displayName §3(Nova seleção)"
            }
            player.sendMessage("§e[Configuração] §aVocê resetou a sua seleção")
            return
        }

        val block = scope.block!!

        if (!selection.containsKey(player.uniqueId)) {
            item.applyMetaData {
                displayName = "$displayName §3(${block.x}, ${block.y}, ${block.z})"
            }

            selection[player.uniqueId] = block
            player.sendMessage("§e[Configuração] §aVocê selecionou o primeiro bloco em §e(${block.location.x}, ${block.location.y}, ${block.location.z})")
            player.updateInventory()
            return
        }

        val storedBlock = selection[player.uniqueId]!!
        val isSameZ = block.z == storedBlock.z
        val isSameX = block.x == storedBlock.x

        if (!isSameZ && !isSameX) {
            player.sendMessage("§cSeleção incorreta! Você precisa selecionar um quadrado plano")
            return
        }

        selection.remove(player.uniqueId)
        item.applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$displayName §3(Nova seleção)"
        }
        player.updateInventory()
        player.sendMessage("§e[Configuração]§a Adicionado blocos dos minigames.")
    }

    override fun getItemCopy(): ItemStack {
        return ItemStack(this.type).apply {
            val meta = Bukkit.getItemFactory().getItemMeta(this.type)
            meta.displayName = "§6${this@WorkshopBlockItemHandler.name}"
            meta.lore = listOf("§7Utilize este item para setar", "§7os blocos do minigame")
            this.itemMeta = meta
        }
    }
}
