package me.sknz.minecraft.extentions

import net.minecraft.server.v1_8_R3.ChatComponentText
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun Player.setTab(header: String = "", bottom: String = "") {
    val connection = (this as CraftPlayer).handle.playerConnection

    val headerComponent = ChatComponentText(header.centerIn(30))
    val bottomComponent = ChatComponentText(bottom.centerIn(30))

    val packet = PacketPlayOutPlayerListHeaderFooter(headerComponent)
    val bottomField = packet::class.declaredMemberProperties
        .find { it.name == "b" } as KMutableProperty<*>?

    bottomField?.setter?.let {
        it.isAccessible = true
        it.call(packet, bottomComponent)
    }

    connection.sendPacket(packet)
}

val Player.playerDirection: BlockFace
    get() {
        val yaw = this.location.yaw.let { (it % 360 + 360) % 360 }

        return when {
            yaw > 135 || yaw < -135 -> BlockFace.NORTH
            yaw < -45 -> BlockFace.EAST
            yaw > 45 -> BlockFace.WEST
            else -> BlockFace.SOUTH
        }
    }

fun String.centerIn(space: Int, char: Char = ' '): String {
    val available = space - length
    val availableStart = available / 2
    return padStart(availableStart + length, char).padEnd(space, char)
}