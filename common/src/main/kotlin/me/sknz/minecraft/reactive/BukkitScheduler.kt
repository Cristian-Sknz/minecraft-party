package me.sknz.minecraft.reactive

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import reactor.core.Disposable
import reactor.core.scheduler.Scheduler
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class BukkitScheduler(private val plugin: Plugin): Scheduler.Worker, Scheduler {

    val tasks = mutableListOf<BukkitTask>()

    override fun dispose() {
        tasks.forEach { it.cancel() }
        tasks.clear()
    }

    override fun createWorker(): Scheduler.Worker {
        return this
    }

    override fun schedule(task: Runnable): Disposable {
        var value: BukkitTask = EmptyTask
        value = Bukkit.getScheduler().runTask(plugin) {
            task.run()
            tasks.remove(value)
        }

        tasks.add(value)

        return Disposable { value.cancel() }
    }

    override fun schedule(task: Runnable, delay: Long, unit: TimeUnit): Disposable {
        var value: BukkitTask = EmptyTask
        value = Bukkit.getScheduler().runTaskLater(plugin, {
            task.run()
            tasks.remove(value)
        }, unit.toMillis(delay).tick)

        tasks.add(value)

        return Disposable { value.cancel() }
    }

    override fun schedulePeriodically(task: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): Disposable {
        val value = Bukkit.getScheduler().runTaskTimer(plugin, {
            task.run()
        }, unit.toMillis(initialDelay).tick, unit.toMillis(period).tick)

        tasks.add(value)

        return Disposable { value.let { it.cancel(); tasks.remove(it) } }
    }

    private val Long.tick: Long
        get() {
            return ((this.toFloat() / 1000F) * 20F).roundToLong()
        }
}