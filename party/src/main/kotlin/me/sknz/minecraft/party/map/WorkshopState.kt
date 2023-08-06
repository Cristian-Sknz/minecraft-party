package me.sknz.minecraft.party.map

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.party.events.GameTimer
import me.sknz.minecraft.party.events.PartyGameState
import me.sknz.minecraft.party.getMatchScoreboard
import me.sknz.minecraft.party.instance
import me.sknz.minecraft.party.model.PartyGameData
import me.sknz.minecraft.scoreboard.BetterScoreboard
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalPluginFeature::class)
class WorkshopState(
    private val manager: ListenerManager,
    private val partyGameData: PartyGameData,
    vararg listeners: (WorkshopState) -> Listener
) : PartyGameState() {

    private val listeners by onMount { listeners.map { it(this) } }
    private val players by onMount(::createWorkshopPlayers)

    private val startingTimer by onMount { GameTimer(10).apply { start() } }
    private val timer by onMount { GameTimer(TimeUnit.MINUTES.toSeconds(2)) }

    data class WorkshopPlayer(
        val player: UUID,
        val name: String,
        val scoreboard: BetterScoreboard,
        var points: Int = 0
    )

    override fun mount() {
        super.mount()

        for ((player, scoreboard) in getAvailablePlayers()) {
            val points = players.subList(0, players.size).map { "§7${it.name}: §a${it.points}" }
            val (name, lines) = getMatchScoreboard("Workshop", player.displayName, points)
            scoreboard.name = name
            scoreboard.setLines(lines)
            scoreboard.setPlayer(player)
        }

        startingTimer.timer.doOnComplete {
            timer.start()

            timer.timer
                .publishOn(instance.async)
                .doFirst {
                    Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, Sound.VILLAGER_HAGGLE, 10F, 5F) }
                }
                .doOnComplete { Bukkit.broadcastMessage("§bPartida encerrada") }
                .filter { it <= 5 }
                .subscribe {
                    Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, Sound.NOTE_BASS, 10F, 5F) }
                }

            timer.timer
                .publishOn(instance.async)
                .filter { it != 0L && it % 15 == 0L }
                .map { it.seconds.toComponents { m, s, _ -> "§a%02dm%02ds".format(m, s) } }
                .subscribe { time ->
                    Bukkit.broadcastMessage("§eA partida será encerrada em ${time}s§e!")
                    Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, Sound.CLICK, 10F, 5F) }
                }
        }.subscribe { time ->
            Bukkit.broadcastMessage("§bWorkshop§e irá iniciar em $time segundo(s)!")
            Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, Sound.NOTE_PLING, 10F, 5F) }
        }

        listeners.forEach(manager::registerAll)
    }

    override fun unmount() {
        listeners.forEach(manager::unregisterAll)
    }

    private fun createWorkshopPlayers(): List<WorkshopPlayer> {
        return partyGameData.players
            .mapNotNull { p -> Bukkit.getPlayer(p.player)?.let { it to p.scoreboard } }
            .map { WorkshopPlayer(it.first.uniqueId, it.first.displayName, it.second) }
    }

    fun getAvailablePlayers(): List<Pair<Player, BetterScoreboard>> {
        return players.mapNotNull { p -> Bukkit.getPlayer(p.player)?.let { it to p.scoreboard } }
    }
}