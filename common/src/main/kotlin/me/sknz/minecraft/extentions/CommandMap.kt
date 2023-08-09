package me.sknz.minecraft.extentions

import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.SimpleCommandMap
import java.util.HashMap
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun CommandMap.unregister(command: Command) {
    if (this !is SimpleCommandMap) return
    val knownCommands = getKnownCommands()
    val commands = knownCommands.entries
        .filter { it.value == command }

    for ((name, value) in commands) {
        knownCommands.remove(name)
        value.unregister(this)
    }
}

fun CommandMap.unregisterAll(command: List<Command>) {
    if (this !is SimpleCommandMap) return

    val knownCommands = getKnownCommands()
    val commands = knownCommands.entries
        .filter { command.any { cmd -> cmd == it.value } }

    for ((name, value) in commands) {
        knownCommands.remove(name)
        value.unregister(this)
    }
}

@Suppress("UNCHECKED_CAST")
private fun SimpleCommandMap.getKnownCommands(): HashMap<String, Command> {
    val property = this::class.declaredMemberProperties
        .find { it.name == "knownCommands" }!!.getter

    property.isAccessible = true

    return property.call(this) as HashMap<String, Command>
}