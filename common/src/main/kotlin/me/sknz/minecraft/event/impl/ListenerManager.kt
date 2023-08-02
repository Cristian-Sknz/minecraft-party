package me.sknz.minecraft.event.impl

import me.sknz.minecraft.event.*
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredListener
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.kotlin.core.publisher.toMono
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Suppress("UNCHECKED_CAST")

class ListenerManager(val plugin: Plugin): LambdaListenerManagerScope, ListenerManagerScope {

    override fun registerAll(listener: Listener) {
        val listeners = getListeners(listener)
            .map { it to it.findAnnotation<EventHandler>()!! }

        for (event in listeners) {
            val callable = event.first
            val handlerList = callable.getEventFromParameter().getHandlerListFromEvent()

            val executor = EventExecutorAdapter { l, e -> callable.call(l, e.toMono()) }
            handlerList.register(RegisteredListener(listener, executor, event.second.priority, plugin, false))
        }
    }

    override fun unregisterAll(listener: Listener) {
        val listeners = getListeners(listener)

        for (event in listeners) {
            event.getEventFromParameter().getHandlerListFromEvent().unregister(listener)
        }
    }

    inline fun <reified T : Event> register(priority: EventPriority = NORMAL): Flux<T> {
        val handlerList = T::class.getHandlerListFromEvent()
        val sink = Sinks.many().unicast().onBackpressureBuffer<T>()
        val executor = ReactorListener(sink)

        handlerList.register(RegisteredListener(executor, executor, priority, plugin, false))
        return sink.asFlux().doOnCancel { handlerList.unregister(executor) }
    }

    override fun <T : Event> register(onEvent: ListenerExecutor<T>) {
        val handlerList = onEvent.getHandlerListFromExecutor()
        val executor = LambdaListener(onEvent)

        handlerList.register(RegisteredListener(executor, executor, NORMAL, plugin, false))
    }

    override fun <T : Event> unregister(onEvent: ListenerExecutor<T>) {
        val handlerList = onEvent.getHandlerListFromExecutor()
        val listener = handlerList.registeredListeners
            .filter { it.listener is LambdaListener<*> }
            .find { (it.listener as LambdaListener<*>).onLambdaEvent == onEvent } ?: return

        handlerList.unregister(listener)
    }

    private fun getListeners(listener: Listener): List<KFunction<*>> {
        return listener::class.declaredFunctions
            .filter { it.hasAnnotation<EventHandler>() }
            .filter { (it.parameters.size - 1) == 1 }
            .filter { it.parameters[1].type.isPublisher() && it.parameters[1].type.isEvent() }
    }

    open class EventExecutorAdapter(val onEvent: (Listener, Event) -> Unit): Listener, EventExecutor {
        override fun execute(listener: Listener, event: Event) = onEvent(listener, event)
    }

    data class LambdaListener<T: Event>(val onLambdaEvent: (Mono<T>) -> Unit)
        : EventExecutorAdapter({ _, e -> onLambdaEvent((e as T).toMono()) })

    data class ReactorListener<T: Event>(val sink: Sinks.Many<T>)
        : EventExecutorAdapter({ _, e -> sink.tryEmitNext(e as T) })
}