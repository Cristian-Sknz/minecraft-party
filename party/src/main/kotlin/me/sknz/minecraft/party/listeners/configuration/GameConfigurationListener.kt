package me.sknz.minecraft.party.listeners.configuration

import me.sknz.minecraft.party.configurations.GameConfiguration
import me.sknz.minecraft.party.map.GameConfigurationState
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

/**
 * ## GameConfigurationListener
 *
 * Classe abstrata que serve de modelo para a criação do ambiente e listeners
 * de configuração de jogos.
 */
abstract class GameConfigurationListener(val state: GameConfigurationState): Listener {
    abstract val configuration: GameConfiguration

    /**
     * Modelo de inventário que será dado aos usuários
     */
    abstract val inventory: Map<Int, ItemStack>
}