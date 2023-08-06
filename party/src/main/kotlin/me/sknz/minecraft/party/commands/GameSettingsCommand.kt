package me.sknz.minecraft.party.commands

import me.sknz.minecraft.party.events.GameStateChange
import me.sknz.minecraft.party.model.PartyGameData.PartyGame
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameSettingsCommand: Command("settings") {

    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cVocê precisa ser um player para executar este comando.")
            return false
        }

        val game = PartyGame[args.getOrNull(0)]
        if (game == null) {
            sender.sendMessage("§cO jogo que você selecionou não é valido.")
            return false
        }

        sender.sendMessage("§aIniciando configurações do modo §e§l${game.name}§a...")
        val event = GameStateChange(GameStateChange.GameState.CONFIGURATION, PartyGame.WORKSHOP)
        Bukkit.getPluginManager().callEvent(event)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
        if (args.isEmpty()) {
            return PartyGame.entries.sortedBy { it.name }.map { it.name }
        }

        if (args.size == 1) {
            return PartyGame.entries
                .sortedBy { it.name }
                .map { it.name }
                .filter { it.equals(args[0], true)}
        }

        return emptyList()
    }
}