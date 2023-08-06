package me.sknz.minecraft.party.listeners

import me.sknz.minecraft.extentions.setTab
import me.sknz.minecraft.party.map.GameStartingState
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import reactor.core.publisher.Mono

class GameStartingListener(private val state: GameStartingState) : Listener {

    @EventHandler
    fun onNewPlayerJoin(event: Mono<PlayerJoinEvent>) = event.doOnNext {
        it.joinMessage = "§7${it.player.displayName}§e entrou no Jogo! (§b${Bukkit.getOnlinePlayers().size}§e/§b8§e)"
    }.map { it.player }.subscribe {
        state.scoreboard.setPlayer(it)
        it.setTab("§e§lPARTY GAMES", "§ethunderplex.net")
        state.scoreboard[6] = "§fJogadores: §a${Bukkit.getOnlinePlayers().size}/8"
    }

    @EventHandler
    fun onPlayerDamage(event: Mono<EntityDamageEvent>) = event.filter { it.entity is Player }
            .subscribe { it.isCancelled = true }
}