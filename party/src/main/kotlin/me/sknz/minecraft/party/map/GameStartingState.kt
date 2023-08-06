package me.sknz.minecraft.party.map

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.party.events.GameStateChange
import me.sknz.minecraft.party.events.GameTimer
import me.sknz.minecraft.party.events.PartyGameState
import me.sknz.minecraft.party.getStartingScoreboard
import me.sknz.minecraft.party.listeners.GameStartingListener
import me.sknz.minecraft.party.scoreboard
import org.bukkit.Bukkit
import org.bukkit.Server
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalPluginFeature::class)
class GameStartingState(
    private val manager: ListenerManager,
    listener: (GameStartingState) -> GameStartingListener
) : PartyGameState() {

    private val timer by onMount { GameTimer(TimeUnit.MINUTES.toSeconds(5)).apply { start() } }
    private val listener by onMount { listener(this) }

    val scoreboard by onMount { scoreboard(getStartingScoreboard()) }

    override fun mount() {
        super.mount()

        timer.timer
            .map { it.seconds.toComponents { m, s, _ -> "§a%02d:%02d".format(m, s) } }
            .doOnComplete {
                if (!this.timer.isStopped)
                    Bukkit.getPluginManager().callEvent(GameStateChange(GameStateChange.GameState.IN_GAME))
            }
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
        timer.isStopped = true
        manager.unregisterAll(listener)
    }
}
