package me.sknz.minecraft.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationDeserializer : JsonDeserializer<Location>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Location {
        val node = p.codec.readTree<JsonNode>(p)
        val world = Bukkit.getWorld(node.get("world").asText())

        val array = node.get("coordinates").map { it.asDouble() }
        if (array.size == 5) {
            return Location(world, array[0], array[1], array[2])
        }

        return Location(world, array[0], array[1], array[2], array[3].toFloat(), array[4].toFloat())
    }
}