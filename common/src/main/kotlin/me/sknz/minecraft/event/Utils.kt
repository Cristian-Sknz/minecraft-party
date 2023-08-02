package me.sknz.minecraft.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.reactivestreams.Publisher
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.*
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.reflect

@OptIn(ExperimentalReflectionOnLambdas::class)
fun <T : Event> ListenerExecutor<T>.getHandlerList(): HandlerList {
    val event = (this.reflect()!!.parameters[0].type.arguments[0].type!!.classifier as KClass<*>)
    val getHandlerList = event.declaredFunctions.find { it.name == "getHandlerList" }
        ?: throw IllegalArgumentException("Sem nenhum evento")

    return getHandlerList.call() as HandlerList
}

fun <T : Event> KClass<T>.getHandlerList(): HandlerList {
    val getHandlerList = this.declaredFunctions.find { it.name == "getHandlerList" }
        ?: this.companionObject?.declaredFunctions?.find { it.name == "getHandlerList" }
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
fun KFunction<*>.getEvent(): KClass<Event> {
    return this.parameters[1].type.arguments[0].type?.classifier!! as KClass<Event>
}