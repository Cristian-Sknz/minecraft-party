package me.sknz.minecraft.party

import me.sknz.minecraft.KotlinPlugin
import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.ListenerManager
import me.sknz.minecraft.party.events.GameStateChange
import me.sknz.minecraft.reactive.BukkitScheduler
import me.sknz.minecraft.reactive.BukkitSchedulerFactory
import org.bukkit.Bukkit
import reactor.core.scheduler.Schedulers
import java.util.concurrent.atomic.AtomicReference

@OptIn(ExperimentalPluginFeature::class)
class Party : KotlinPlugin() {

    val sync by onLoad(::BukkitScheduler)
    val async by onLoad { BukkitSchedulerFactory(this).apply { Schedulers.setFactory(this) }.scheduler }
    private val manager by onEnable(::ListenerManager)

    override fun onEnable() {
        reference.set(this)
        super.onEnable()

        manager.registerAll(GameController(manager))

        Bukkit.getLogger().info("[party] Plugin carregado")
        Bukkit.getPluginManager().callEvent(GameStateChange(GameStateChange.GameState.STARTING))
    }
}

private val reference = AtomicReference<Party>()
val instance: Party
    get() = reference.get()
