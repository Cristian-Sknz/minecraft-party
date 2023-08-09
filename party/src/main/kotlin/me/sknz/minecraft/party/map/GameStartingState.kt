package me.sknz.minecraft.party.map

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.extentions.unregisterAll
import me.sknz.minecraft.party.commands.GameSettingsCommand
import me.sknz.minecraft.party.commands.GameTimerCommand
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

    private val listener by onMount { listener(this) }

    val timer by onMount { GameTimer(TimeUnit.MINUTES.toSeconds(3)) }
    val scoreboard by onMount { scoreboard(getStartingScoreboard()) }
    val commands by onMount { listOf(GameSettingsCommand(), GameTimerCommand(timer)) }

    override fun mount() {
        super.mount()

        timer.timer
            .map { it.seconds.toComponents { m, s, _ -> "§a%02d:%02d".format(m, s) } }
            .doOnComplete {
                if (this.timer.isComplete) Bukkit.getPluginManager()
                    .callEvent(GameStateChange(GameStateChange.GameState.IN_GAME))
            }
            .subscribe { scoreboard[4] = "§fIniciando em §a${it}" }

        timer.timer
            .filter { it != 0L && it % 15 == 0L }
            .map { it.seconds.toComponents { m, s, _ -> "§a%02dm%02ds".format(m, s) } }
            .subscribe {
                Bukkit.broadcast("§eA partida será iniciada em ${it}§e!", Server.BROADCAST_CHANNEL_USERS)
            }

        manager.registerAll(listener)
        Bukkit.getCommandMap().registerAll("party", commands)
    }

    override fun unmount() {
        timer.stop()
        manager.unregisterAll(listener)
        Bukkit.getCommandMap().unregisterAll(commands)
    }
}
