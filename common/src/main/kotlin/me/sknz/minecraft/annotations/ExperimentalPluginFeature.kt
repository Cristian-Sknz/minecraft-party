package me.sknz.minecraft.annotations

import kotlin.annotation.AnnotationTarget.*

/**
 * # ExperimentalPluginFeature
 *
 * Anotação utilizada para sinalizar que uma feature não está completa ou é experimental,
 * pode ser modificada ou removida posteriormente.
 */
@RequiresOptIn
@MustBeDocumented
@Target(
    CLASS,
    ANNOTATION_CLASS,
    PROPERTY,
    FIELD,
    LOCAL_VARIABLE,
    VALUE_PARAMETER,
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY_GETTER,
    PROPERTY_SETTER,
    TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalPluginFeature()
