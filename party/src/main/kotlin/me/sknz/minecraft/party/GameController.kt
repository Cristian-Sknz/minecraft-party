package me.sknz.minecraft.party

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.party.events.GameStateChange
import me.sknz.minecraft.party.events.GameStateChange.GameState.*
import me.sknz.minecraft.party.events.PartyGameState
import me.sknz.minecraft.party.listeners.GameStartingListener
import me.sknz.minecraft.party.listeners.GameWorkshopListener
import me.sknz.minecraft.party.listeners.configuration.GameWorkshopConfiguration
import me.sknz.minecraft.party.map.GameConfigurationState
import me.sknz.minecraft.party.map.GameStartingState
import me.sknz.minecraft.party.map.WorkshopState
import me.sknz.minecraft.party.model.PartyGameData
import me.sknz.minecraft.party.model.PartyGameData.PartyGame.WORKSHOP
import me.sknz.minecraft.party.model.PartyPlayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import reactor.core.publisher.Mono

@OptIn(ExperimentalPluginFeature::class)
class GameController(private val manager: ListenerManager) : Listener {

    private var actual: PartyGameState? = null

    @EventHandler
    fun game(event: Mono<GameStateChange>) = event.subscribeOn(instance.sync).subscribe { e ->
        when (e.state) {
            CONFIGURATION -> {
                val game = e.payload as PartyGameData.PartyGame
                actual!!.unmount()
                when (game) {
                    WORKSHOP -> actual = GameConfigurationState(manager, game, ::GameWorkshopConfiguration).apply { mount() }
                }
            }
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

                setActualGame(gameData)
            }

            else -> {
                actual?.unmount()
            }
        }
    }

    fun setActualGame(data: PartyGameData) {
        when (data.games[data.game]) {
            WORKSHOP -> {
                actual = WorkshopState(manager, data, ::GameWorkshopListener).apply { mount() }
            }
        }
    }

    fun GameStateChange.getGameData(): PartyGameData {
        return this.payload as PartyGameData?
            ?: PartyGameData(Bukkit.getOnlinePlayers().map { PartyPlayer(it.uniqueId) }, -1)
    }
}