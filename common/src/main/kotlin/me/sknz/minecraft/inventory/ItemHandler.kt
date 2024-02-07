package me.sknz.minecraft.inventory

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*

@ExperimentalPluginFeature
abstract class ItemHandler(val type: Material,
                           val name: String? = null,
                           val actions: EnumSet<Action> = SELECT_AND_CLICK,
                           val cancel: Boolean = true
) {

    companion object {
        val SELECT_AND_CLICK: EnumSet<Action> = EnumSet.of(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK)
        val RIGHT_CLICK: EnumSet<Action> = EnumSet.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
        val LEFT_CLICK: EnumSet<Action> = EnumSet.of(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK)
        val ALL_CLICK: EnumSet<Action> =  EnumSet.of(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
    }

    abstract fun onClick(player: Player, item: ItemStack, scope: ItemHandlerScope)

    open fun getItemCopy(): ItemStack = throw NotImplementedError()
    open fun onUpdateItem(player: Player, item: ItemStack): Unit = throw NotImplementedError()

    data class ItemHandlerScope(val action: Action,
                           val block: Block? = null,
                           val blockFace: BlockFace? = null) {
        constructor(e: PlayerInteractEvent): this(e.action, e.clickedBlock, e.blockFace)

        fun isRightClick(): Boolean {
            return RIGHT_CLICK.any { this.action == it }
        }

        fun isLeftClick(): Boolean {
            return LEFT_CLICK.any { this.action == it }
        }
    }
}