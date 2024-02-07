package me.sknz.minecraft.party

import me.sknz.minecraft.scoreboard.BetterScoreboard
import org.bukkit.Bukkit
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias ScoreboardData = Pair<String, String>

const val MINIGAME_SCOREBOARD = "Party Games"
const val SITE = "thunderplex.net"

fun scoreboard(data: ScoreboardData): BetterScoreboard {
    return BetterScoreboard(Bukkit.getScoreboardManager().newScoreboard, data.first, data.second)
}

fun getStartingScoreboard(): ScoreboardData {
    return "§e§l${MINIGAME_SCOREBOARD.uppercase()}" to """
        §7${LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy"))}
        
        §fMapa: §a${MINIGAME_SCOREBOARD}
        §fJogadores: §a0/8
        
        §fIniciando em §a03:00
        §fpara dar tempo a
        §fjogadores adicionais
        
        §e${SITE}
    """.trimIndent()
}

fun getMatchScoreboard(game: String, player: String, players: List<String>): ScoreboardData {
    return "§e§l${MINIGAME_SCOREBOARD.uppercase()}" to """
        §7${LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy"))}
        
        §fGame: 
        §a${game}
        
        ${players.joinToString("\n") { "$it§f" }}
        §f...
        §7${player}: §a0
        
        §fJogos: 1/4
        §fEstrelas: §a 0
        
        §e${SITE}
    """.trimIndent()
}

fun getConfigurationScoreboard(game: String): ScoreboardData {
    return "§e§l${MINIGAME_SCOREBOARD.uppercase()}" to """
        §7${LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy"))}
        
        §fGame: §a§l${game}

        §fVocê está no modo
        §fde configuração do
        §fparty games.
        
        §e${SITE}
    """.trimIndent()
}