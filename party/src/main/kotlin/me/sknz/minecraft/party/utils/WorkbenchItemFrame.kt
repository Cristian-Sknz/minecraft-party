package me.sknz.minecraft.party.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import java.util.*
import kotlin.math.abs
import kotlin.math.floor

typealias BlockWithFace = Pair<Block, BlockFace>

/**
 * ## WorkbenchItemFrame
 *
 * Classe responsável pela criação de molduras que renderizam
 * o crafting de um item em um plano 3x3 (x, y ou z, y).
 * Esta mecanica é utilizada no minigame [me.sknz.minecraft.party.map.WorkshopState].
 */
class WorkbenchItemFrame(selection: Pair<BlockWithFace, BlockWithFace>) {

    constructor(selection: List<Location>, face: BlockFace):
            this((selection[0].block to face) to (selection[1].block to face))

    private val blocks: Array<Block>
    var recipe: ShapedRecipe? = null
        set(value) {
            field = value
            setItemFramesRecipe(value)
        }

    init {
        if (!isValid(selection)) {
            throw RuntimeException("A seleção de blocos não está valida.")
        }
        val (b1, f1) = selection.first
        val b2 = selection.second.first

        val isSameZ = b1.z == b2.z

        this.blocks = b1.get2DSelectionBlocks(b2, f1, isSameZ)
        this.recipe = null
    }

    private fun setItemFramesRecipe(recipe: ShapedRecipe? = null) {
        val frames = blocks[4].let { it.world.getNearbyEntities(it.location, it.x.toDouble(), it.y.toDouble(), it.z.toDouble()) }
            .filterIsInstance<ItemFrame>()
            .distinct()

        val shape = recipe?.shape
        val ingredients = recipe?.ingredientMap

        for ((index, item) in blocks.withIndex()) {
            val frame = frames.find { it.location blockEquals item.location } ?:
                item.world.spawnEntity(item.location, EntityType.ITEM_FRAME) as ItemFrame

            val row = floor(index.toDouble() / 3).toInt()
            val col = index % 3

            val abc = shape?.let {
                if (row < it.size) it[row] else null
            }

            if (recipe == null) {
                frame.item = ItemStack(Material.AIR)
                continue
            }

            if (abc != null && col + 1 > abc.length) {
                frame.item = ItemStack(Material.AIR)
                continue
            }

            frame.item = ItemStack(ingredients!![abc!![col]]?.type ?: Material.AIR)
        }
    }

    private fun isValid(selection: Pair<BlockWithFace, BlockWithFace>): Boolean {
        val (b1, f1) = selection.first
        val (b2, f2) = selection.second

        val isSameZ = b1.z == b2.z
        val isSameX = b1.x == b2.x

        if (!isSameZ && !isSameX) {
            return false
        }

        if (f1 != f2) {
            return false
        }

        if (b1.y distance b2.y != 3) {
            return false
        }

        if (isSameZ && b1.x distance b2.x != 3) {
            return false
        }

        return !(isSameX && b1.z distance b2.z != 3)
    }

    private fun Block.get2DSelectionBlocks(other: Block, face: BlockFace, axisX: Boolean): Array<Block> {
        val blocks = TreeSet<Block>(compareBy({ -it.y }, { if (axisX) it.x else it.z }))
        val world = this.location.world

        for (xz in 0..2) for (y in 0..2) {
            val block = world.getBlockAt(
                if (axisX) this.x.plus(other.x, xz) else this.x + face.modX,
                this.y.plus(other.y, y),
                if (!axisX) this.z.plus(other.z, xz) else this.z + face.modZ
            )

            blocks.add(block)
        }

        return blocks.toTypedArray()
    }

    private infix fun Location.blockEquals(other: Location) = blockX == other.blockX
            && blockY == other.blockY
            && blockZ == other.blockZ

    private infix fun Int.distance(other: Int) = abs(this - other) + 1

    private fun Int.plus(other: Int, value: Int): Int {
        return if (this - other < 0) this + value else this - value
    }
}