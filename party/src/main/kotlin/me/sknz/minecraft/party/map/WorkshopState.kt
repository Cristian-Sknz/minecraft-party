package me.sknz.minecraft.party.map

import com.google.common.collect.Iterators
import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.extentions.BlockFaceUtils.getBlockFace
import me.sknz.minecraft.extentions.BlockFaceUtils.yaw
import me.sknz.minecraft.extentions.EntityUtils.freeze
import me.sknz.minecraft.extentions.EntityUtils.spawnEntity
import me.sknz.minecraft.party.configurations.WorkshopConfiguration
import me.sknz.minecraft.party.events.GameTimer
import me.sknz.minecraft.party.events.PartyGameState
import me.sknz.minecraft.party.getMatchScoreboard
import me.sknz.minecraft.party.instance
import me.sknz.minecraft.party.map.model.WorkshopItem
import me.sknz.minecraft.party.map.model.WorkshopPlayer
import me.sknz.minecraft.party.model.PartyGameData
import me.sknz.minecraft.party.utils.WorkbenchItemFrame
import me.sknz.minecraft.scoreboard.BetterScoreboard
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
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

    private val startingTimer by onMount { GameTimer(10) }
    private val timer by onMount { GameTimer(TimeUnit.MINUTES.toSeconds(2)) }

    val items by onMount { WorkshopItem.getItems().shuffled() }
    val configuration by onMount { WorkshopConfiguration.load() }

    override fun mount() {
        super.mount()

        val spawns = configuration.spawns.iterator()
        val elements = items.slice(0..5)
        val size = (configuration.blocks.size * 25) / 100

        val materials = Iterators.cycle(elements.flatMap { it.materials })
        val blocks = configuration.blocks.indices.shuffled().map {
            return@map if (it < size) IndexedValue(it, materials.next())
            else IndexedValue(it, items.random().materials.filter { m -> m != Material.LOG }.randomOrNull() ?: Material.WEB)
        }

        for ((player, scoreboard) in getAvailablePlayers()) {
            val spawn = spawns.next() ?: configuration.location
            val points = players.subList(0, players.size).map { "§7${it.name}: §a${it.points}" }
            val (name, lines) = getMatchScoreboard("Workshop", player.displayName, points)

            scoreboard.name = name
            scoreboard.setLines(lines)
            scoreboard.setPlayer(player)
            player.teleport(spawn)

            spawnVillager(configuration.villager.apply { yaw = spawn.getBlockFace().oppositeFace.yaw })
            val frame = WorkbenchItemFrame(configuration.frame, spawn.getBlockFace().oppositeFace)
            frame.recipe = Bukkit.getRecipesFor(ItemStack(elements[0].material))[0] as ShapedRecipe

            player.sendMessage("Item a ser craftados: ${elements.joinToString(", ") { it.material.name.lowercase() }}")
            player.sendMessage("${blocks.size}: $size")
            blocks.forEach { (i, material) -> configuration.blocks[i].block.type = material }
        }

        startingTimer.play()

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
                Bukkit.broadcastMessage("§eA partida será encerrada em ${time}§e!")
                Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, Sound.CLICK, 10F, 5F) }
            }

        startingTimer.timer.doOnComplete(timer::play).subscribe { time ->
            Bukkit.broadcastMessage("§bWorkshop§e irá iniciar em $time segundo(s)!")
            Bukkit.getOnlinePlayers().forEach { it.playSound(it.location, Sound.NOTE_PLING, 10F, 5F) }
        }

        listeners.forEach(manager::registerAll)
    }

    override fun unmount() {
        listeners.forEach(manager::unregisterAll)
    }

    private fun spawnVillager(location: Location) {
        val entity = location.world.spawnEntity<Villager>(location)

        entity.profession = Villager.Profession.LIBRARIAN
        entity.customName = "Mestre em Engenharia"

        entity.isCustomNameVisible = true
        entity.freeze()
    }

    private fun createWorkshopPlayers(): List<WorkshopPlayer> {
        return partyGameData.players
            .mapNotNull { p -> Bukkit.getPlayer(p.player)?.let { it to p.scoreboard } }
            .map { WorkshopPlayer(it.first.uniqueId, it.second) }
    }

    fun getAvailablePlayers(): List<Pair<Player, BetterScoreboard>> {
        return players.mapNotNull { p -> p.player?.let { it to p.scoreboard } }
    }
}