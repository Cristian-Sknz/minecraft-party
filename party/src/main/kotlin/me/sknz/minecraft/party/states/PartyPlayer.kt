package me.sknz.minecraft.party.states

import me.sknz.minecraft.scoreboard.BetterScoreboard
import org.bukkit.Bukkit
import java.util.*

data class PartyPlayer(val player: UUID) {

    val scoreboard: BetterScoreboard = BetterScoreboard(Bukkit.getScoreboardManager().newScoreboard)
    var stars: Int = 0

}