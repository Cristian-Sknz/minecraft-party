package me.sknz.minecraft.party.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class GameStateChange(var state: GameState, val payload: Any? = null) : Event() {

    enum class GameState {
        STARTING,
        IN_GAME,
        FINISHING,
    }

    override fun getHandlers(): HandlerList {
        return GameStateChange.handler
    }

    companion object {

        val handler = HandlerList()

        @JvmStatic
        fun getHandlerList() = handler
    }
}