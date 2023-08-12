package me.sknz.minecraft.party.listeners

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.party.map.GameStartingState
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import reactor.core.publisher.Mono

class GameStartingListener(private val state: GameStartingState) : Listener {

    @EventHandler
    fun onNewPlayerJoin(event: Mono<PlayerJoinEvent>) = event.doOnNext {
        it.joinMessage = "§7${it.player.displayName}§e entrou no Jogo! (§b${Bukkit.getOnlinePlayers().size}§e/§b8§e)"
        state.scoreboard[6] = "§fJogadores: §a${Bukkit.getOnlinePlayers().size}/8"
    }.map { it.player }.subscribe(state::setStateToPlayer)

    @OptIn(ExperimentalPluginFeature::class)
    @EventHandler
    fun onTimerStart(event: Mono<PlayerJoinEvent>) = event.subscribe {
        if (Bukkit.getOnlinePlayers().size >= 4) {
            state.gameTimer.play()
            return@subscribe
        }
    }

    @OptIn(ExperimentalPluginFeature::class)
    @EventHandler
    fun onTimerPause(event: Mono<PlayerQuitEvent>) = event.subscribe {
        if (Bukkit.getOnlinePlayers().size >= 4) {
            return@subscribe
        }
        it.quitMessage = ""
        state.gameTimer.pause()
        state.gameTimer.reset()
    }

    @EventHandler
    fun onPlayerDamage(event: Mono<EntityDamageEvent>) = event.filter {
        it.entity is Player
    }.subscribe { it.isCancelled = true }

    @EventHandler
    fun onPlayerDrop(event: Mono<PlayerDropItemEvent>) = event.filter {
        it.player.gameMode != GameMode.CREATIVE
    }.subscribe { it.isCancelled = true }

    @EventHandler
    fun onPlayerBreakBlocks(event: Mono<BlockBreakEvent>) = event.filter {
        it.player.gameMode != GameMode.CREATIVE
    }.subscribe { it.isCancelled = true }

}