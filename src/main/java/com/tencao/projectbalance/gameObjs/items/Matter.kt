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

package com.tencao.projectbalance.gameObjs.items

import moze_intel.projecte.gameObjs.items.Matter
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class Matter: Matter() {
    private val names = arrayOf("dark", "red", "blue")

    override fun getTranslationKey(stack: ItemStack): String {
        return super.getTranslationKey() + "_" + names[stack.itemDamage]
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(cTab: CreativeTabs?, list: NonNullList<ItemStack>) {
        if (isInCreativeTab(cTab)) {
            for (i in 0..2) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }
}