package me.sknz.minecraft.party.commands

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.party.events.GameTimer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@OptIn(ExperimentalPluginFeature::class)
class GameTimerCommand(private val timer: GameTimer)
    : Command("timer", "", "/timer <set/start/pause/stop> [args]", emptyList()) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.")
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage("§cComando incorreto! Tente utilizar: $usageMessage")
            return false
        }

        return when (args[0]) {
            "set" -> {
                sender.sendMessage("§cAinda não existe uma implementação para está função.")
                true
            }
            "pause" -> {
                timer.pause()
                sender.sendMessage("§aVocê pausou o timer da partida.")
                true
            }
            "start" -> {
                timer.play()
                sender.sendMessage("§aVocê iniciou o timer da partida.")
                true
            }
            "stop" -> {
                timer.end()
                sender.sendMessage("§aVocê parou o timer da partida.")
                true
            }
            else -> {
                sender.sendMessage("§cUsage: $usageMessage")
                false
            }
        }
    }
}
