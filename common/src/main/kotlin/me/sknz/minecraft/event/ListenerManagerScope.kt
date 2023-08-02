package me.sknz.minecraft.event

import org.bukkit.event.Event
import org.bukkit.event.Listener
import reactor.core.publisher.Mono

typealias ListenerExecutor<T> = (Mono<T>) -> Unit

interface ListenerManagerScope {
    fun registerAll(listener: Listener)
    fun unregisterAll(listener: Listener)
}

interface LambdaListenerManagerScope {
    fun <T : Event> register(onEvent: ListenerExecutor<T>)
    fun  <T : Event> unregister(onEvent: ListenerExecutor<T>)
}