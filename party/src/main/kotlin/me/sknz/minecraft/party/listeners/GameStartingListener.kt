package me.sknz.minecraft.party.listeners

import me.sknz.minecraft.party.map.GameStartingState
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import reactor.core.publisher.Mono

class GameStartingListener(private val state: GameStartingState) : Listener {

    @EventHandler
    fun onNewPlayerJoin(event: Mono<PlayerJoinEvent>) {
        event.doOnNext { it.joinMessage = "§7${it.player.displayName}§e entrou (§b${Bukkit.getOnlinePlayers().size}§e/§b8§e) no Jogo!" }
            .map { it.player }
            .subscribe {
                state.scoreboard.setPlayer(it)
                state.scoreboard[6] = "§fJogadores: §a${Bukkit.getOnlinePlayers().size}/8"
            }
    }
}
