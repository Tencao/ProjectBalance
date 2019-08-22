/*
 * Copyright (C) 2018
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tencao.projectbalance.gameObjs.items.itemBlocks

import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack

class ItemPedestalBlock(block: Block) : ItemBlock(block) {
    init {
        this.maxDamage = 0
        this.hasSubtypes = true
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return when (stack.itemDamage) {
            1 -> "tile.pe_rm_pedestal"
            2 -> "tile.pe_bm_pedestal"
            else -> "tile.pe_dm_pedestal"
        }
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }
}