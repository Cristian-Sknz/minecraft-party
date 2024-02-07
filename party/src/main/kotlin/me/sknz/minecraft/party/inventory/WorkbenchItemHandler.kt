package me.sknz.minecraft.party.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.inventory.applyMetaData
import me.sknz.minecraft.party.configurations.WorkshopConfiguration
import me.sknz.minecraft.party.configurations.WorkshopPlayerConfiguration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.abs

@OptIn(ExperimentalPluginFeature::class)
class WorkbenchItemHandler(val configuration: WorkshopPlayerConfiguration, private val state: Map<UUID, Int?>)
    : ItemHandler(Material.WORKBENCH, "Quadro de Crafting", SELECT_AND_CLICK) {

    private val selection: HashMap<UUID, Pair<Block, BlockFace>> = hashMapOf()

    override fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope) {
        if (state[player.uniqueId] == null) {
            player.sendMessage("§cVocê precisa selecionar um spawn para configurar o quadro.")
            return
        }

        if (scope.isLeftClick()) {
            onUpdateItem(player, item)
            player.sendMessage("§e[Configuração] §aVocê resetou a sua seleção")
            return
        }

        player.updateInventory()

        val block = scope.block!!
        val face = scope.blockFace!!

        if (!selection.containsKey(player.uniqueId)) {
            player.sendMessage("§e[Configuração]§aVocê selecionou o primeiro bloco em §e(${block.location.x}, ${block.location.y}, ${block.location.z}, $face)")
            item.applyMetaData {
                displayName = displayName.substringBefore(" §3(")
                displayName = "$displayName §3(${block.x}, ${block.y}, ${block.z})"
            }

            selection[player.uniqueId] = block to face
            player.updateInventory()
            return
        }

        if (!isValidClick(player, scope.block!!, scope.blockFace!!)) {
            return
        }

        val storedBlock = selection[player.uniqueId]!!.first
        configuration[state[player.uniqueId]!!].frame = listOf(storedBlock.location, block.location)
        onUpdateItem(player, item)
        player.sendMessage("§e[Configuração] §aNova posição do §6Quadro de Crafting§a foi setado.")
    }

    fun isValidClick(player: Player, block: Block, face: BlockFace): Boolean {
        val (storedBlock, storedFace) = selection[player.uniqueId]!!
        val isSameZ = block.z == storedBlock.z
        val isSameX = block.x == storedBlock.x

        if (!isSameZ && !isSameX) {
            player.sendMessage("§cSeleção incorreta! Você precisa selecionar um quadrado plano de 3x3")
            return false
        }

        if (face != storedFace) {
            player.sendMessage("§cO bloco precisa estar virado para o mesmo lado do primeiro.")
            return false
        }

        if (block.y distance storedBlock.y < 3) {
            player.sendMessage("§cO bloco precisa ter pelo menos ter 3 de altura.")
            return false
        }

        if ((isSameZ && block.x distance storedBlock.x < 3) ||
            (isSameX && block.z distance storedBlock.z < 3)) {
            player.player.sendMessage("§cO bloco precisa ter pelo menos ter 3 de distância.")
            return false
        }

        return true
    }

    override fun onUpdateItem(player: Player, item: ItemStack) {
        selection.remove(player.uniqueId)
        item.applyMetaData {
            displayName = displayName.substringBefore(" §3(")
        }
        player.updateInventory()
    }

    private infix fun Int.distance(other: Int) = abs(this - other) + 1

    override fun getItemCopy(): ItemStack {
        return ItemStack(this.type).apply {
            val meta = Bukkit.getItemFactory().getItemMeta(this.type)
            meta.displayName = "§6${this@WorkbenchItemHandler.name}"
            meta.lore = listOf("§7Utilize este item para setar", "§7o spawn do villager de troca")
            this.itemMeta = meta
        }
    }
}