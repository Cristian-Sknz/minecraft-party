package me.sknz.minecraft.party

import me.sknz.minecraft.KotlinPlugin
import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.party.events.GameStateChange
import org.bukkit.Bukkit
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

@OptIn(ExperimentalPluginFeature::class)
class Party : KotlinPlugin() {

    override fun onEnable() {
        reference.set(this)
        super.onEnable()

        manager.registerAll(GameController(manager))

        Bukkit.getLogger().info("[party] Plugin carregado")
        Bukkit.getPluginManager().callEvent(GameStateChange(GameStateChange.GameState.STARTING))
    }

    private infix fun Int.distance(other: Int) = abs(this - other) + 1
}

private val reference = AtomicReference<Party>()
val instance: Party
    get() = reference.get()
