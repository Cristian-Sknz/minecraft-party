package me.sknz.minecraft.party.listeners.configuration

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.inventory.ItemHandler
import me.sknz.minecraft.party.configurations.GameConfiguration
import me.sknz.minecraft.party.inventory.WorkbenchItemHandler
import me.sknz.minecraft.party.inventory.WorkshopBlockItemHandler
import me.sknz.minecraft.party.inventory.WorkshopPlayerPositionItem
import me.sknz.minecraft.party.inventory.WorkshopVillagerPositionItem
import me.sknz.minecraft.party.map.GameConfigurationState
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import reactor.core.publisher.Mono
import java.util.*

@OptIn(ExperimentalPluginFeature::class)
class GameWorkshopConfiguration(state: GameConfigurationState) : GameConfigurationListener(state) {

    private val handlers: List<ItemHandler> = listOf(
        WorkbenchItemHandler(),
        WorkshopBlockItemHandler(),
        WorkshopPlayerPositionItem(),
        WorkshopVillagerPositionItem()
    )

    override val inventory: Map<Int, ItemStack> by workshopInventory()

    override val configuration: GameConfiguration = object : GameConfiguration {
        override var location = Bukkit.getWorlds()[0].spawnLocation
            .apply {
                this.x = (-1797).toDouble()
                this.y = 59.toDouble()
                this.z = 739.toDouble()
            }

    }

    private fun workshopInventory(): Lazy<TreeMap<Int, ItemStack>> {
        return lazyOf(TreeMap<Int, ItemStack>().apply {
            put(0, handlers.find { it.type == Material.MONSTER_EGG }!!.getItemCopy())
            put(3, handlers.find { it.type == Material.SKULL_ITEM }!!.getItemCopy())
            put(4, handlers.find { it.type == Material.WORKBENCH }!!.getItemCopy())
            put(5, handlers.find { it.type == Material.NAME_TAG }!!.getItemCopy())
            put(8, ItemStack(Material.BED).apply {
                val meta = Bukkit.getItemFactory().getItemMeta(this.type)
                meta.displayName = "§6Concluir as Configurações"
                meta.lore = listOf("§7Utilize este item para concluir", "§7a configuração desde modo")
                this.itemMeta = meta
            })
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