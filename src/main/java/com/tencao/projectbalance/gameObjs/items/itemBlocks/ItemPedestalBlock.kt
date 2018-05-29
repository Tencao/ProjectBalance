package com.tencao.projectbalance.gameObjs.items.itemBlocks

import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack

class ItemPedestalBlock(block: Block) : ItemBlock(block) {
    init {
        this.maxDamage = 0
        this.hasSubtypes = true
    }

    override fun getUnlocalizedName(stack: ItemStack?): String {
        return when (stack!!.itemDamage) {
            1 -> "tile.pe_rm_pedestal"
            2 -> "tile.pe_bm_pedestal"
            else -> "tile.pe_dm_pedestal"
        }
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }
}