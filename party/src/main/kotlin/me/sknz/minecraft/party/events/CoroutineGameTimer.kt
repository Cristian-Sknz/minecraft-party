package me.sknz.minecraft.party.events

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import me.sknz.minecraft.party.instance
import java.util.concurrent.CancellationException

class CoroutineGameTimer(private val seconds: Int) {

    private val _time = MutableStateFlow(0)
    val time: StateFlow<Int>
        get() = _time

    private var job: Job? = null

    suspend fun start() {
        if ((job != null && job!!.isActive) || time.value - seconds < 0) return

        job = instance.launch(instance.asyncDispatcher) {
            repeat(seconds) {
                _time.value += 1
                delay(1000)
            }
        }
    }

    fun pause() {
        if (job == null || !job!!.isActive) return

        job!!.cancel(CancellationException("A execução do timer foi pausada."))
    }

    fun stop() {
        if (job == null || !job!!.isActive) {
            _time.value = 0
            return
        }

        _time.value = 0
        job!!.cancel(CancellationException("A execução do timer foi parada."))
    }

}