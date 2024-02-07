package me.sknz.minecraft.party.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.inventory.applyMetaData
import me.sknz.minecraft.party.configurations.WorkshopConfiguration
import me.sknz.minecraft.party.configurations.WorkshopPlayerConfiguration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

@OptIn(ExperimentalPluginFeature::class)
class WorkshopBlockItemHandler(val configuration: WorkshopPlayerConfiguration, private val state: Map<UUID, Int?>)
    : ItemHandler(Material.NAME_TAG, "Blocos de Interação", SELECT_AND_CLICK) {

    private val selection: HashMap<UUID, Block> = hashMapOf()

    override fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope) {
        if (state[player.uniqueId] == null) {
            player.sendMessage("§cVocê precisa selecionar um spawn para configurar os blocos.")
            return
        }

        if (scope.isLeftClick()) {
            onUpdateItem(player, item)
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
        configuration[state[player.uniqueId]!!].frame = listOf(storedBlock.location, block.location)

        val minY = minOf(storedBlock.y, block.y)
        val maxY = maxOf(storedBlock.y, block.y)

        val minXZ = minOf(
            if (isSameX) storedBlock.z else storedBlock.x,
            if (isSameX) block.z else block.x,
        )
        val maxXZ = maxOf(
            if (isSameX) storedBlock.z else storedBlock.x,
            if (isSameX) block.z else block.x,
        )

        val locations = mutableListOf<Vector>()
        for (xz in minXZ..maxXZ) for (y in minY..maxY) {
            locations.add(block.world.getBlockAt(
                if (isSameX) block.x else xz,
                y,
                if (!isSameX) block.z else xz
            ).location.toVector())
        }

        configuration[state[player.uniqueId]!!].blocks.addAll(locations)
        onUpdateItem(player, item)
        player.sendMessage("§e[Configuração]§a Adicionado blocos dos minigames.")
    }


    override fun onUpdateItem(player: Player, item: ItemStack) {
        selection.remove(player.uniqueId)
        item.applyMetaData {
            displayName = displayName.substringBefore(" §3(")
            displayName = "$displayName §3(Nova seleção)"
        }

        player.updateInventory()
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
