package me.sknz.minecraft.event

import org.bukkit.event.Listener

interface ListenerManagerScope {
    fun registerAll(listener: Listener)
    fun unregisterAll(listener: Listener)
}