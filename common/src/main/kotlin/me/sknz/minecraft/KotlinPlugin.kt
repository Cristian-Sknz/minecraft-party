package me.sknz.minecraft

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@ExperimentalPluginFeature
open class KotlinPlugin: JavaPlugin() {

    private val onLoads: MutableList<PluginReadOnlyProperty<Plugin, Plugin, *>> = mutableListOf()
    private val onEnables: MutableList<PluginReadOnlyProperty<Plugin, Plugin, *>> = mutableListOf()

    fun <T> onLoad(initializer: (Plugin) -> T): PluginReadOnlyProperty<Plugin, Plugin, T> {
        return PluginReadOnlyProperty<Plugin, Plugin, T>(initializer).let { onLoads.add(it); it }
    }

    fun <T> onEnable(initializer: (Plugin) -> T): PluginReadOnlyProperty<Plugin, Plugin, T> {
        return PluginReadOnlyProperty<Plugin, Plugin, T>(initializer).let { onEnables.add(it); it }
    }

    override fun onLoad() {
        onLoads.forEach { it.initialize(this) }
        super.onLoad()
    }

    override fun onEnable() {
        onEnables.forEach { it.initialize(this) }
        super.onEnable()
    }

    @ExperimentalPluginFeature
    class PluginReadOnlyProperty<P, K, V>(private val initializer: (P) -> V) : ReadOnlyProperty<K, V> {
        private var value: V? = null

        fun initialize(args: P) {
            this.value = initializer(args)
        }

        override fun getValue(thisRef: K, property: KProperty<*>): V {
            if (value == null)
                throw UninitializedPropertyAccessException("Esta propriedade ainda não foi inicializada")

            return value!!
        }
    }
}