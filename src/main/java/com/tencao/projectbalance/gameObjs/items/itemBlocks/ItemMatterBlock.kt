package com.tencao.projectbalance.gameObjs.items.itemBlocks

import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack

class ItemMatterBlock(block: Block) : ItemBlock(block) {
    init {
        this.maxDamage = 0
        this.hasSubtypes = true
    }

    override fun getUnlocalizedName(stack: ItemStack?): String {
        return when (stack!!.itemDamage) {
            1 -> "tile.pe_rm_block"
            2 -> "tile.pe_bm_block"
            else -> "tile.pe_dm_block"
        }
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }
}
