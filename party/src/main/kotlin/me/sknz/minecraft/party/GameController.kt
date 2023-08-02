package me.sknz.minecraft.party

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.ListenerManager
import me.sknz.minecraft.party.events.GameListener
import me.sknz.minecraft.party.events.GameStateChange
import me.sknz.minecraft.party.events.GameStateChange.GameState.*
import me.sknz.minecraft.party.map.GameStartingState
import me.sknz.minecraft.party.listeners.GameStartingListener
import me.sknz.minecraft.party.listeners.GameWorkshopListener
import me.sknz.minecraft.party.map.WorkshopState
import me.sknz.minecraft.party.states.PartyGameData
import me.sknz.minecraft.party.states.PartyGameData.PartyGame.WORKSHOP
import me.sknz.minecraft.party.states.PartyPlayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import reactor.core.publisher.Mono

@OptIn(ExperimentalPluginFeature::class)
class GameController(private val manager: ListenerManager) : Listener {

    private var actual: GameListener? = null

    @EventHandler
    fun game(event: Mono<GameStateChange>) = event.subscribeOn(instance.sync).subscribe { e ->
        when (e.state) {
            STARTING -> {
                actual = GameStartingState(manager, ::GameStartingListener).apply { mount() }
            }

            IN_GAME -> {
                val gameData = e.getGameData()
                gameData.game++
                actual!!.unmount()

                if (gameData.game >= gameData.games.size) {
                    Bukkit.getPluginManager().callEvent(e.apply { e.state = FINISHING })
                    return@subscribe
                }

                when (gameData.games[gameData.game]) {
                    WORKSHOP -> {
                        actual = WorkshopState(manager, gameData, ::GameWorkshopListener).apply { mount() }
                    }
                }
            }

            else -> {
                actual?.unmount()
            }
        }
    }

    fun GameStateChange.getGameData(): PartyGameData {
        return this.payload as PartyGameData?
            ?: PartyGameData(Bukkit.getOnlinePlayers().map { PartyPlayer(it.uniqueId) }, -1)
    }
}