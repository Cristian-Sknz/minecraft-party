package me.sknz.minecraft.extentions

import me.sknz.minecraft.extentions.BlockFaceUtils.getBlockFace
import me.sknz.minecraft.scoreboard.BetterScoreboard
import net.minecraft.server.v1_8_R3.ChatComponentText
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * ## [PlayerUtils]
 *
 * Classe de utilidades para a entidade [Player] do Bukkit
 */
object PlayerUtils {
    /**
     * Aplicar uma [BetterScoreboard] para um player
     */
    fun Player.setScoreboard(betterScoreboard: BetterScoreboard) {
        betterScoreboard.setPlayer(this)
    }

    /**
     * Aplicar uma tablist para um player
     */
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

    /**
     * Pegar a direção do player em [BlockFace]
     */
    val Player.playerDirection: BlockFace get() = this.location.getBlockFace()

    private fun String.centerIn(space: Int, char: Char = ' '): String {
        val available = space - length
        val availableStart = available / 2
        return padStart(availableStart + length, char).padEnd(space, char)
    }
}