package me.sknz.minecraft.party.map

import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.party.events.GameListener
import me.sknz.minecraft.party.events.GameStateChange
import me.sknz.minecraft.party.events.GameTimer
import me.sknz.minecraft.party.getStartingScoreboard
import me.sknz.minecraft.party.scoreboard
import me.sknz.minecraft.party.listeners.GameStartingListener
import org.bukkit.Bukkit
import org.bukkit.Server
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalPluginFeature::class)
class GameStartingState(
    private val manager: ListenerManager,
    listener: (GameStartingState) -> GameStartingListener
) : GameListener() {

    private val timer by onMount { GameTimer(30).apply { start() } }
    private val listener by onMount { listener(this) }

    val scoreboard by onMount { scoreboard(getStartingScoreboard()) }

    override fun mount() {
        super.mount()

        timer.timer
            .map { it.seconds.toComponents { m, s, _ -> "§a%02d:%02d".format(m, s) } }
            .doOnComplete { Bukkit.getPluginManager().callEvent(GameStateChange(GameStateChange.GameState.IN_GAME)) }
            .subscribe { scoreboard[4] = "§fIniciando em §a${it}" }

        timer.timer
            .filter { it != 0L && it % 15 == 0L }
            .map { it.seconds.toComponents { m, s, _ -> "§a%02dm%02ds".format(m, s) } }
            .subscribe {
                Bukkit.broadcast("§eA partida será iniciada em ${it}s§e!", Server.BROADCAST_CHANNEL_USERS)
            }

        manager.registerAll(listener)
    }

    override fun unmount() {
        timer.isStopped = false
        manager.unregisterAll(listener)
    }
}
