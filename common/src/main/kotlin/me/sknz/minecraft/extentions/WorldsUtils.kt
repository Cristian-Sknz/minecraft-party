package me.sknz.minecraft.extentions

import me.sknz.minecraft.extentions.LocationUtils.clientYaw
import me.sknz.minecraft.extentions.LocationOperators.plus
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

/**
 * ## [LocationUtils]
 *
 * Classe de utilidades para localizações e vectors no Bukkit
 */
object LocationUtils {

    /**
     * Transforma o eixo vertical de 360º
     * para -180º/180º. Esta é a forma que funciona no cliente do minecraft.
     */
    val Location.clientYaw: Float get() = let {
        return if (normalizedYaw > 180) normalizedYaw - 360 else normalizedYaw
    }

    /**
     * Normaliza o eixo vertical de uma localização
     */
    val Location.normalizedYaw: Float get() = (yaw % 360 + 360) % 360

    /**
     * Vira o eixo vertical de uma localização para o lado oposto.
     */
    fun Location.opposite(): Location {
        return this.apply { yaw = (yaw + 180) % 360 }
    }

    /**
     * Rotaciona um vector para a posição onde está o eixo vertical e o
     * eixo horizontal.
     *
     * @param yaw eixo vertical
     * @param pitch eixo horizontal
     *
     * @return O vetor com as mudanças aplicadas
     */
    fun Vector.rotate(yaw: Float, pitch: Float = 0F): Vector {
        val yawRad = toRadians(-yaw.toDouble())
        val pitchRad = toRadians(pitch.toDouble())

        val newX = x * cos(yawRad) - z * sin(yawRad)
        val newZ = x * sin(yawRad) + z * cos(yawRad)

        val newX2 = newX * cos(pitchRad) + y * sin(pitchRad)
        val newY = -newX * sin(pitchRad) + y * cos(pitchRad)

        return Vector(newX2, newY, newZ)
    }

    /**
     * Pega a posição relativa de uma localização
     * aplicando o offset e mudanças nos eixos.
     */
    fun Location.getRelativeLocation(offset: Vector): Location {
        return this.clone() + (offset.rotate(this.yaw))
    }

    /**
     * Pega a posição relativa de uma localização
     * aplicando o offset e mudanças nos eixos.
     */
    fun Location.getRelativeLocation(offset: Location): Location {
        return this.getRelativeLocation(offset.toVector())
    }

    /**
     * Pega o offset de uma região para a outra
     *
     * @return Offset de uma região
     */
    fun Location.getOffset(loc: Location): Location {
        val newX = this.x - loc.x
        val newY = this.y - loc.y
        val newZ = this.z - loc.z

        return Location(this.world, newX, newY, newZ, this.yaw, this.pitch)
    }
}

/**
 * ## [BlockFaceUtils]
 *
 * Classe de utilidades para a classe [BlockFace] do Bukkit
 */
object BlockFaceUtils {

    /**
     * Pegar o eixo vertical de uma direção ([BlockFace])
     *
     * Essa função suporta apenas as 4 direções principais
     *  - [BlockFace.NORTH]
     *  - [BlockFace.EAST]
     *  - [BlockFace.WEST]
     *  - [BlockFace.SOUTH]
     *
     *  Caso a região não for nenhuma dessas especificadas, sempre irá retornar [BlockFace.SOUTH]
     */
    val BlockFace.yaw: Float get() = when (this) {
        BlockFace.NORTH -> 180F
        BlockFace.EAST -> 90F
        BlockFace.WEST -> -90F
        else -> 0F
    }

    /**
     * Transforma o block face em um vector com a direção
     */
    fun BlockFace.toDirection(): Vector {
        return Vector(this.modX, this.modY, this.modZ)
    }

    /**
     * Pega o blockface de uma região em relação a outra.
     */
    fun Location.getBlockFace(location: Location): BlockFace {
        val modX = compareValues(this.blockX, location.blockX)
        val modY = compareValues(this.blockZ, location.blockZ)

        return BlockFace.entries.find { it.modX == modX && it.modY == 0 && it.modZ == modY }!!
    }

    /**
     * Pega o blockface de uma região utilizando a eixo vertical.
     */
    fun Location.getBlockFace() = when(clientYaw) {
        in -180.0..-135.0, in 135.0..180.0 -> BlockFace.NORTH
        in -135.0..-45.0 -> BlockFace.EAST
        in -45.0..45.0 -> BlockFace.SOUTH
        in 45.0..135.0 -> BlockFace.WEST
        else -> BlockFace.SELF
    }
}

/**
 * ## [LocationOperators]
 *
 * Classe de utilidades para facilitar os operadores de uma [Location] do Bukkit
 */
object LocationOperators {

    /**
     * Soma uma localização com a outra. Isso cria uma nova localização.
     */
    operator fun Location.plus(loc: Location): Location {
        return this.clone().add(loc)
    }

    /**
     * Soma uma localização com a um vector. Isso cria uma nova localização.
     */
    operator fun Location.plus(vec: Vector): Location {
        return this.clone().add(vec)
    }

    /**
     * Subtrai uma localização com a outra. Isso cria uma nova localização.
     */
    operator fun Location.minus(loc: Location): Location {
        return this.clone().subtract(loc)
    }

    /**
     * Subtrai uma localização com um vector. Isso cria uma nova localização.
     */
    operator fun Location.minus(vec: Vector): Location {
        return this.clone().subtract(vec)
    }

    /**
     * Soma uma localização com a outra e aplica as mudanças.
     */
    operator fun Location.plusAssign(loc: Location) {
        this.add(loc)
    }

    /**
     * Soma uma localização com um vector e aplica as mudanças.
     */
    operator fun Location.plusAssign(vec: Vector) {
        this.add(vec)
    }

    /**
     * Subtrai uma localização com a outra e aplica as mudanças.
     */
    operator fun Location.minusAssign(loc: Location) {
        this.subtract(loc)
    }

    /**
     * Subtrai uma localização com a outra e aplica as mudanças.
     */
    operator fun Location.minusAssign(vec: Vector) {
        this.subtract(vec)
    }
}