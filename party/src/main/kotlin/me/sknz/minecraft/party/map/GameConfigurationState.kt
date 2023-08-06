package me.sknz.minecraft.party.map

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.event.impl.ListenerManager
import me.sknz.minecraft.party.events.PartyGameState
import me.sknz.minecraft.party.getConfigurationScoreboard
import me.sknz.minecraft.party.listeners.configuration.GameConfigurationListener
import me.sknz.minecraft.party.model.PartyGameData
import me.sknz.minecraft.party.scoreboard
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.github.paperspigot.Title

@OptIn(ExperimentalPluginFeature::class)
class GameConfigurationState(
    private val listenerManager: ListenerManager,
    game: PartyGameData.PartyGame,
    listener: (GameConfigurationState) -> GameConfigurationListener,
) : PartyGameState() {

    private val listener by onMount { listener(this) }
    val scoreboard by onMount { scoreboard(getConfigurationScoreboard(game.name)) }

    override fun mount() {
        super.mount()
        listenerManager.registerAll(listener)

        for (player in Bukkit.getOnlinePlayers()) {
            setForPlayer(player)
        }

        Bukkit.broadcastMessage("§e[Configuração]§a Modo de configuração iniciado com sucesso.")
    }

    fun setForPlayer(player: Player) {
        player.inventory.clear()
        player.teleport(listener.configuration.location)
        scoreboard.setPlayer(player)

        if (!player.hasPermission("party.configuration")) {
            player.gameMode = GameMode.SPECTATOR
            player.sendMessage("§cVocê não tem permissões suficientes para participar da configuração.")
            return
        }
        player.gameMode = GameMode.SURVIVAL
        player.allowFlight = true
        player.sendTitle(Title("§3Modo de Configuração"))
        listener.inventory.forEach { (slot, item) ->
            player.inventory.setItem(slot, ItemStack(item))
        }
    }

    override fun unmount() {
        listenerManager.unregisterAll(listener)
    }
}