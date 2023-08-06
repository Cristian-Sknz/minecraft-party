package me.sknz.minecraft.inventory

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun ItemStack.applyMetaData(block: ItemMeta.(ItemStack) -> Unit) {
    val clone = this.itemMeta.clone()
    block(clone, this)
    this.itemMeta = clone
}