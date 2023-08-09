package me.sknz.minecraft.party.events

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import me.sknz.minecraft.party.instance
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.TimeUnit

typealias GameTime = Long

@ExperimentalPluginFeature
class GameTimer(val initialTime: GameTime) {

    private var task: Disposable? = null

    private val sinks = Sinks.many()
        .multicast()
        .onBackpressureBuffer<GameTime>()

    val timer: Flux<GameTime>
        get() = sinks.asFlux()

    var time: GameTime = initialTime
        private set
    var isComplete: Boolean = false
        private set
    var isPaused: Boolean = false
        private set
    var isStopped: Boolean = false
        private set

    fun play() {
        if (task != null) {
            return
        }

        isPaused = false
        task = instance.async.schedulePeriodically({
            if (time == 0L) {
                isComplete = true
                sinks.tryEmitComplete()
                task!!.dispose()
                return@schedulePeriodically
            }
            sinks.tryEmitNext(time - 1)
            time--
        }, 0, 1, TimeUnit.SECONDS)
    }

    fun stop() {
        if (isComplete) return
        task?.dispose()
        isStopped = true
        sinks.tryEmitComplete()
    }

    fun end() {
        if (isComplete || isStopped) return
        task?.dispose()
        time = 0
        isComplete = true
        sinks.tryEmitComplete()
    }

    fun pause() {
        if (task == null) return
        task!!.dispose()
        task = null
        isPaused = true
    }

    fun reset() {
        if (isComplete || isStopped) return
        time = initialTime
    }
}