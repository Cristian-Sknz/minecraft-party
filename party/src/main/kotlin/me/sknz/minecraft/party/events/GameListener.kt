package me.sknz.minecraft.party.events

import me.sknz.minecraft.annotations.ExperimentalPluginFeature
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * # GameListener
 *
 * Classe abstrada responsável em gerenciar os eventos e
 * ciclos de vida de um game.
 */
@ExperimentalPluginFeature
abstract class GameListener {

    private val mounts = mutableListOf<MountReadonlyProperty<*>>()

    /**
     * Função responsável em delegar propriedades de um GameListener,
     * a instanciação das propriedades, ocorrem na montagem do ciclo de vida.
     *
     * @see mount
     */
    fun <T> onMount(init: () -> T): MountReadonlyProperty<T> {
        return MountReadonlyProperty(init).let { mounts.add(it); it}
    }

    /**
     * Função responsável em iniciar o ciclo de vida de um GameListener.
     * É recomendado iniciar todas as dependencias e eventos, apartir desta função.
     */
    open fun mount() {
        mounts.forEach { it.initialize() }
    }

    /**
     * Função responsável em destruir o ciclo de vida de um GameListener.
     * Pode ser utilizado para remover estados e eventos inicializados no game.
     *
     */
    abstract fun unmount()

    @ExperimentalPluginFeature
    class MountReadonlyProperty<V>(private val init: () -> V) : ReadOnlyProperty<GameListener, V> {

        var mounted: V? = null

        fun initialize() = let { mounted = init() }

        override fun getValue(thisRef: GameListener, property: KProperty<*>): V {
            return mounted ?: throw RuntimeException()
        }
    }
}