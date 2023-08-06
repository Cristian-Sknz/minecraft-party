package me.sknz.minecraft

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import org.bukkit.Bukkit

@OptIn(ExperimentalPluginFeature::class)
class Common: KotlinPlugin() {
    val alive by onEnable { Bukkit.getLogger().info("[$it] $it iniciado com sucesso.") }
}