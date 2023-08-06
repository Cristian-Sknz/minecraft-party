package me.sknz.minecraft.party.listeners.configuration

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.party.configurations.WorkshopConfiguration
import me.sknz.minecraft.party.inventory.*
import me.sknz.minecraft.party.map.GameConfigurationState
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import reactor.core.publisher.Mono
import java.util.*

@OptIn(ExperimentalPluginFeature::class)
class GameWorkshopConfiguration(state: GameConfigurationState) : GameConfigurationListener(state) {

    override val configuration = WorkshopConfiguration.load()

    private val handlers: List<ItemHandler> = listOf(
        SaveConfigurationItemHandler(configuration),
        WorkbenchItemHandler(configuration),
        WorkshopBlockItemHandler(configuration),
        WorkshopPlayerPositionItem(configuration),
        WorkshopVillagerPositionItem(configuration)
    )

    override val inventory: Map<Int, ItemStack> by workshopInventory()

    private fun workshopInventory(): Lazy<TreeMap<Int, ItemStack>> {
        return lazyOf(TreeMap<Int, ItemStack>().apply {
            put(0, handlers.find { it.type == Material.MONSTER_EGG }!!.getItemCopy())
            put(3, handlers.find { it.type == Material.SKULL_ITEM }!!.getItemCopy())
            put(4, handlers.find { it.type == Material.WORKBENCH }!!.getItemCopy())
            put(5, handlers.find { it.type == Material.NAME_TAG }!!.getItemCopy())
            put(8, handlers.find { it.type == Material.BED }!!.getItemCopy())
        })
    }

    @EventHandler
    fun onPlayerJoin(e: Mono<PlayerJoinEvent>) = e
        .map(PlayerJoinEvent::getPlayer)
        .subscribe(state::setForPlayer)

    @EventHandler
    fun onPlayerSetSpawn(e: Mono<PlayerInteractEvent>) {
        e.subscribe()
    }

    @EventHandler
    fun onItemHandlerClick(e: Mono<PlayerInteractEvent>) = e.mapNotNull { event ->
        handlers.find { event.isItemInteraction(it.type, it.name!!, it.actions) }?.let { it to event }
    }.subscribe { (handler, event) ->
        event.isCancelled = handler.cancel
        handler.onClick(event.player, event.item, ItemHandler.ItemHandlerScope(event))
    }

    @EventHandler
    fun onItemHandlerDrop(e: Mono<PlayerDropItemEvent>) = e.filter { event ->
        handlers.any { event.itemDrop.itemStack.isItemHandler(it.type, it.name!!) }
    }.subscribe { event ->
        event.isCancelled = true
    }

    private fun ItemStack.isItemHandler(type: Material, name: String): Boolean {
        return this.type == type && ChatColor
            .stripColor(this.itemMeta.displayName)
            .startsWith(name, true)
    }

    private fun PlayerInteractEvent.isItemInteraction(
        type: Material, name: String,
        actions: EnumSet<Action> = EnumSet.of(Action.RIGHT_CLICK_BLOCK)
    ): Boolean {
        return this.item.isItemHandler(type, name) && actions.any { it == this.action }
    }
}