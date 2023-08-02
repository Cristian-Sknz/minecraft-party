package me.sknz.minecraft.reactive

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

object EmptyTask : BukkitTask {
    override fun getTaskId(): Int {
        throw NotImplementedError("Not yet implemented")
    }

    override fun getOwner(): Plugin {
        throw NotImplementedError("Not yet implemented")
    }

    override fun isSync(): Boolean {
        throw NotImplementedError("Not yet implemented")
    }

    override fun cancel() {
        throw NotImplementedError("Not yet implemented")
    }
}