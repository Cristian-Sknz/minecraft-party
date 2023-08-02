package me.sknz.minecraft.party.events

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import reactor.core.publisher.Flux
import java.time.Duration

typealias GameTime = Long

@ExperimentalPluginFeature
class GameTimer(val initialTime: GameTime) {

    private lateinit var _timer: Flux<GameTime>

    lateinit var timer: Flux<GameTime>
        private set

    var time: GameTime = initialTime
        private set

    var isStopped: Boolean = false

    fun start() {
        _timer = Flux.interval(Duration.ofSeconds(1))
            .takeUntil { it >= initialTime || isStopped }
            .map { initialTime - it }
            .doOnNext { time = it }

        timer = _timer.share()
    }
}
