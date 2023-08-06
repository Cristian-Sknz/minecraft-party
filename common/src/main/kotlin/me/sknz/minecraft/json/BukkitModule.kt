package me.sknz.minecraft.json

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleSerializers
import com.fasterxml.jackson.databind.ser.Serializers
import org.bukkit.Location

class BukkitModule: Module() {

    private val serializers: Serializers = SimpleSerializers(listOf<JsonSerializer<*>>(
        LocationSerializer
    ))
    private val deserializers: Deserializers = SimpleDeserializers(hashMapOf<Class<*>,JsonDeserializer<*>>(
        Location::class.java to LocationDeserializer
    ))

    override fun version(): Version {
        return Version(1,0,0, "", "me.sknz.minecraft", "json")
    }

    override fun getModuleName(): String {
        return "BukkitJackson"
    }

    override fun setupModule(context: SetupContext) {
        context.addSerializers(serializers)
        context.addDeserializers(deserializers)
    }
}