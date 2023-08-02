package me.sknz.minecraft.event

import org.bukkit.event.Event

interface LambdaListenerManagerScope {
    fun <T : Event> register(onEvent: ListenerExecutor<T>)
    fun  <T : Event> unregister(onEvent: ListenerExecutor<T>)
}