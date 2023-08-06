package me.sknz.minecraft.party

import me.sknz.minecraft.KotlinPlugin
import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.party.commands.GameSettingsCommand
import me.sknz.minecraft.party.events.GameStateChange
import me.sknz.minecraft.party.utils.WorkbenchItemFrame
import me.sknz.minecraft.reactive.BukkitScheduler
import me.sknz.minecraft.reactive.BukkitSchedulerFactory
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.material.MaterialData
import reactor.core.scheduler.Schedulers
import java.io.File
import java.util.Comparator
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs
import kotlin.math.floor

@OptIn(ExperimentalPluginFeature::class)
class Party : KotlinPlugin() {

    val sync by onLoad(::BukkitScheduler)
    val async by onLoad { BukkitSchedulerFactory(this).apply { Schedulers.setFactory(this) }.scheduler }
    private val manager by onEnable(::ListenerManager)

    override fun onEnable() {
        reference.set(this)
        super.onEnable()

        manager.registerAll(GameController(manager))

        Bukkit.getCommandMap().register("settings", GameSettingsCommand())

        Bukkit.getLogger().info("[party] Plugin carregado")
        Bukkit.getPluginManager().callEvent(GameStateChange(GameStateChange.GameState.STARTING))
    }

    private infix fun Int.distance(other: Int) = abs(this - other) + 1
}

private val reference = AtomicReference<Party>()
val instance: Party
    get() = reference.get()
