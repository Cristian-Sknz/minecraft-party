package me.sknz.minecraft

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.jnbt.*
import java.io.InputStream

@Suppress("DEPRECATION")
data class Schematic(
    val blocks: ShortArray,
    val data: ByteArray,
    val width: Short,
    val height: Short,
    val length: Short,
) {

    fun generate(location: Location): List<Block> {
        return dimension { x, y, z, index ->
            val block = Location(location.world, x + location.x, y + location.y, z + location.z).block
            val material = Material.entries.find { it.id.toShort() == blocks[index] }
                ?: return@dimension block;

            block.type = material

            return@dimension block
        }
    }

    private fun <T> dimension(function: (Int, Int, Int, Int) -> T): List<T> {
        val result = mutableListOf<T>()
        for (x in 0..< width) for (y in 0..< height) for (z in 0..< length)
            result.add(function(x, y, z, y * width * length + z * width + x))

        return result
    }

    companion object {
        fun getSchematic(input: InputStream): Schematic {
            val nbtStream = NBTInputStream(input)
            val schematicTag = nbtStream.readTag() as CompoundTag
            nbtStream.close()

            if (!schematicTag.name.equals("schematic", true)) {
                throw IllegalArgumentException("Este arquivo não é uma schematic")
            }

            val schematic = schematicTag.value
            if (!schematic.containsKey("Blocks")) {
                throw NoSuchElementException("Não há blocos neste schematic")
            }

            val width = schematic.getChildTag<ShortTag>("Width").value
            val height = schematic.getChildTag<ShortTag>("Height").value
            val length = schematic.getChildTag<ShortTag>("Length").value

            val blocksId = schematic.getChildTag<ByteArrayTag>("Blocks").value
            val data = schematic.getChildTag<ByteArrayTag>("Data").value
            val addId = if (schematic.containsKey("AddBlocks")) {
                schematic.getChildTag<ByteArrayTag>("AddBlocks").value
            } else byteArrayOf()

            val blocks = blocksId.indices.map { index ->
                return@map if (index shr 1 >= addId.size) {
                    (blocksId[index].toInt() and 0xFF).toShort()
                } else if (index and 0x1 == 0) {
                    ((addId[index shr 1].toInt() and 0xF shl 8) + (blocksId[index].toInt() and 0xFF)).toShort()
                } else {
                    ((addId[index shr 1].toInt() and 0xF0 shl 4) + (blocksId[index].toInt() and 0xFF)).toShort()
                }
            }.toShortArray()

            return Schematic(blocks, data, width, height, length)
        }

        inline fun <reified T : Tag> Map<String, Tag>.getChildTag(key: String): T {
            if(!this.containsKey(key)) {
                throw NoSuchElementException("A chave $key não existe neste NBT.")
            }

            val tag = this[key]!!
            if (T::class.isInstance(tag)) {
                return (tag as T)
            }

            throw RuntimeException("O tipo ${T::class.simpleName} não corresponde a chave $key com o tipo ${tag::class.simpleName}")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Schematic

        if (!blocks.contentEquals(other.blocks)) return false
        if (!data.contentEquals(other.data)) return false
        if (width != other.width) return false
        if (height != other.height) return false
        return length == other.length
    }

    override fun hashCode(): Int {
        var result = blocks.contentHashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + length
        return result
    }

    override fun toString(): String {
        return "Schematic(blocks=${blocks.size}, data=${data.size}, width=$width, height=$height, length=$length)"
    }
}
