package me.sknz.minecraft.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.bukkit.Location

object LocationSerializer : JsonSerializer<Location>() {

    override fun serialize(value: Location, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject().let {
            gen.writeStringField("world", value.world.name)
            gen.writeArrayFieldStart("coordinates").let {
                gen.writeNumber(value.x)
                gen.writeNumber(value.y)
                gen.writeNumber(value.z)
                gen.writeNumber(value.yaw)
                gen.writeNumber(value.pitch)
            }
            gen.writeEndArray()
        }
        gen.writeEndObject()
    }

    override fun handledType(): Class<Location> = Location::class.java
}