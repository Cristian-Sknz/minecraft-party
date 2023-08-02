package me.sknz.minecraft.scoreboard

import com.google.common.base.Splitter
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class BetterScoreboard (private val scoreboard: Scoreboard, name: String? = null, initial: String? = null) {

    private val objective: Objective = scoreboard.registerNewObjective("BetterScoreboard", "dummy")
    private val lines: Array<Pair<Team, String>?> = arrayOfNulls(16)
    init {
        objective.displaySlot = DisplaySlot.SIDEBAR
        name?.let { this.name = it }
        initial?.let(::setLines)
    }

    var name: String
        get() = objective.displayName
        set(value) = let { objective.displayName = value }

    companion object {
        const val BLANK = " "
    }

    operator fun set(line: Int, value: String?) {
        if (value == null) {
            remove(line)
            return
        }

        if (lines[line] != null) {
            update(line, value)
            return
        }

        val team = scoreboard.registerNewTeam()
        val text = value.withoutDuplicates()
        objective.getScore(team.id).score = line

        team.setDisplayValues(text)
        lines[line] = team to value
    }

    private fun update(line: Int, value: String) {
        val team = lines[line]!!.first
        val text = value.withoutDuplicates()
        team.setDisplayValues(text)

        lines[line] = team to value
    }

    fun remove(line: Int) {
        val pair = lines[line] ?: return
        pair.first.unregister()
        scoreboard.resetScores(pair.first.id)
        lines[line] = null
    }

    fun setLines(text: String) {
        val values = text.lines().reversed()
        for (line in values.size - 1 downTo 0) {
            this[line] = values[line].ifBlank { BLANK }
        }
    }

    infix fun setPlayer(player: Player) {
        player.scoreboard = this.scoreboard
    }

    private fun Team.setDisplayValues(text: String) {
        if (text.length > 16) {
            val values = Splitter.fixedLength(16).split(text).iterator()
            val prefix = values.next()
            val suffix = values.next()

            val insertColor = prefix.endsWith('§')

            this.prefix = if(insertColor) prefix.removeSuffix("§") else prefix
            this.suffix = if(insertColor) "§$suffix" else "${ChatColor.getLastColors(this.prefix)}$suffix"
        } else {
            this.prefix = text
            this.suffix = ""
        }
    }

    private fun Scoreboard.registerNewTeam(): Team {
        val id = ChatColor.entries.find { color -> this.teams.find { it.hasEntry(color.toString()) } == null }!!
        val team = this.registerNewTeam(id.toString())

        team.id = id.toString()

        return team
    }

    private fun String.withoutDuplicates(): String {
        var value = this
        while (lines.find { it?.second == value } != null) {
            value += "§r"
        }

        return value
    }

    private var Team.id: String
        get() = this@id.entries.first()
        set(value) {
            this@id.entries.forEach(this@id::removeEntry)
            this@id.addEntry(value)
        }
}