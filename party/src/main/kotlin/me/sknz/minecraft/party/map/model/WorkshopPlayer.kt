package me.sknz.minecraft.party.map.model

import me.sknz.minecraft.scoreboard.BetterScoreboard
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

data class WorkshopPlayer(val uuid: UUID, val scoreboard: BetterScoreboard) {
    val player: Player? get() = Bukkit.getPlayer(uuid)
    val name: String = player!!.name

    var points: Int = 0
}