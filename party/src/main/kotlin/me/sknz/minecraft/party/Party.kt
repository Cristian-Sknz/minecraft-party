package me.sknz.minecraft.party

import com.fasterxml.jackson.databind.ObjectMapper
import me.sknz.minecraft.KotlinPlugin
import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.json.BukkitModule
import me.sknz.minecraft.party.commands.GameSettingsCommand
import me.sknz.minecraft.party.events.GameStateChange
import me.sknz.minecraft.reactive.BukkitScheduler
import me.sknz.minecraft.reactive.BukkitSchedulerFactory
import org.bukkit.Bukkit
import reactor.core.scheduler.Schedulers
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

@OptIn(ExperimentalPluginFeature::class)
class Party : KotlinPlugin() {

    val sync by onLoad(::BukkitScheduler)
    val async by onLoad { BukkitSchedulerFactory(this).apply { Schedulers.setFactory(this) }.scheduler }
    val mapper by onLoad { ObjectMapper().apply { registerModule(BukkitModule()) } }

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
