package me.sknz.minecraft.reactive

import org.bukkit.plugin.Plugin
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ThreadFactory

class BukkitSchedulerFactory(val plugin: Plugin) : Schedulers.Factory {

    val scheduler = AsyncBukkitScheduler(plugin)

    override fun newBoundedElastic(
        threadCap: Int,
        queuedTaskCap: Int,
        threadFactory: ThreadFactory,
        ttlSeconds: Int
    ): Scheduler {
        return scheduler
    }

    override fun newParallel(parallelism: Int, threadFactory: ThreadFactory): Scheduler {
        return scheduler
    }

    override fun newSingle(threadFactory: ThreadFactory): Scheduler {
        return scheduler
    }
}
