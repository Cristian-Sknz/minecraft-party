package me.sknz.minecraft.event

import me.sknz.minecraft.event.impl.ListenerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import reactor.core.publisher.Mono
import java.time.Duration

class ListenerManagerTest {

    val manager by lazy { ListenerManager(mock()) }

    @BeforeEach
    fun clearHandlers() {
        val handlerList = PlayerJoinEvent.getHandlerList()
        handlerList.registeredListeners.forEach { handlerList.unregister(it) }
    }

    @Test
    fun register_listener() {
        val handlerList = PlayerJoinEvent.getHandlerList()

        manager.registerAll(object : Listener {
            @EventHandler
            fun hello(e: Mono<PlayerJoinEvent>): Unit = let {}
        })
        Assertions.assertEquals(1, handlerList.registeredListeners.size)
    }

    @Test
    fun unregister_listener() {
        val handlerList = PlayerJoinEvent.getHandlerList()

        val listener = object : Listener {
            @EventHandler
            fun hello(e: Mono<PlayerJoinEvent>): Unit = let {}
        }

        manager.registerAll(listener)
        manager.unregisterAll(listener)

        Assertions.assertEquals(0, handlerList.registeredListeners.size)
    }

    @Test
    fun call_listener_event() {
        val handlerList = PlayerJoinEvent.getHandlerList()
        val event = PlayerJoinEvent(mock(), "")

        var join = false
        val listener = object : Listener {
            @EventHandler
            fun hello(e: Mono<PlayerJoinEvent>): Unit = let { join = true }
        }

        manager.registerAll(listener)

        handlerList.registeredListeners.forEach { it.callEvent(event) }
        Assertions.assertEquals(true, join)
    }

    @Test
    fun register_lambda_event() {
        val handlerList = PlayerJoinEvent.getHandlerList()
        manager.register<PlayerJoinEvent> { }

        Assertions.assertEquals(1, handlerList.registeredListeners.size)
    }

    @Test
    fun unregister_lambda_event() {
        val handlerList = PlayerJoinEvent.getHandlerList()
        val event: ListenerExecutor<PlayerJoinEvent> = {}

        manager.register(event)
        manager.unregister(event)

        Assertions.assertEquals(0, handlerList.registeredListeners.size)
    }

    @Test
    fun call_lambda_event() {
        val handlerList = PlayerJoinEvent.getHandlerList()
        val event = PlayerJoinEvent(mock(), "")

        var join = false
        manager.register<PlayerJoinEvent> { join = true }

        handlerList.registeredListeners.forEach { it.callEvent(event) }
        Assertions.assertEquals(true, join)
    }

    @Test
    fun register_reified_event() {
        val handlerList = PlayerJoinEvent.getHandlerList()
        manager.register<PlayerJoinEvent>()
            .map { it.joinMessage }
            .subscribe()

        Assertions.assertEquals(1, handlerList.registeredListeners.size)
    }

    @Test
    fun unregister_reified_event() {
        val handlerList = PlayerJoinEvent.getHandlerList()
        val disposable = manager.register<PlayerJoinEvent>()
            .map { it.joinMessage }
            .subscribe()

        Assertions.assertEquals(1, handlerList.registeredListeners.size)
        disposable.dispose()
        Assertions.assertEquals(0, handlerList.registeredListeners.size)
    }

    @Test
    fun call_reified_event() {
        val handlerList = PlayerJoinEvent.getHandlerList()
        val event = PlayerJoinEvent(mock(), "")

        val flux = manager.register<PlayerJoinEvent>()

        handlerList.registeredListeners.forEach { it.callEvent(event) }
        Assertions.assertDoesNotThrow { flux.blockFirst(Duration.ofSeconds(1)) }
    }
}