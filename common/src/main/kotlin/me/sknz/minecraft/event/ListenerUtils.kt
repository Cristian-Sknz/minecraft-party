package me.sknz.minecraft.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.*
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.reflect

typealias ListenerExecutor<T> = (Mono<T>) -> Unit

const val HANDLERS_FIELD = "getHandlerList"

@OptIn(ExperimentalReflectionOnLambdas::class)
fun <T : Event> ListenerExecutor<T>.getHandlerListFromExecutor(): HandlerList {
    val event = (this.reflect()!!.parameters[0].type.arguments[0].type!!.classifier as KClass<*>)
    val getHandlerList = event.declaredFunctions.find { it.name == HANDLERS_FIELD }
        ?: throw IllegalArgumentException("Sem nenhum evento")

    return getHandlerList.call() as HandlerList
}

fun <T : Event> KClass<T>.getHandlerListFromEvent(): HandlerList {
    val getHandlerList = this.declaredFunctions.find { it.name == HANDLERS_FIELD }
        ?: this.companionObject?.declaredFunctions?.find { it.name == HANDLERS_FIELD }
        ?: this.superclasses.firstNotNullOfOrNull { it.declaredFunctions.find { fn -> fn.name == HANDLERS_FIELD } }
        ?: throw IllegalArgumentException("Não foi encontrado nenhuma lista de manipulação (getHandlerList)")

    getHandlerList.isAccessible = true

    if (getHandlerList.parameters.size == 1) {
        return getHandlerList.call(this.companionObjectInstance) as HandlerList
    }

    return getHandlerList.call() as HandlerList
}


fun KType.isPublisher(): Boolean {
    return (this.classifier as KClass<*>).isSubclassOf(Publisher::class)
}

fun KType.isEvent(): Boolean {
    return (this.arguments[0].type!!.classifier as KClass<*>).isSubclassOf(Event::class)
}

@Suppress("UNCHECKED_CAST")
fun KFunction<*>.getEventFromParameter(): KClass<Event> {
    return this.parameters[1].type.arguments[0].type?.classifier!! as KClass<Event>
}