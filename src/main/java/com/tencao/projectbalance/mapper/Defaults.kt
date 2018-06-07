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

package com.tencao.projectbalance.mapper

import moze_intel.projecte.emc.SimpleStack
import net.minecraft.init.Items
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack

object Defaults {

    val values = hashMapOf(
            Pair(SimpleStack(ItemStack(Items.GOLD_INGOT)), 1),
            Pair(SimpleStack(ItemStack(Items.GOLD_NUGGET)), 1),
            Pair(SimpleStack(ItemStack(Items.REDSTONE)), 1)
    )

    val complexities = hashMapOf(
            Pair(SimpleStack(ItemStack(Items.GOLD_INGOT)), 1),
            Pair(SimpleStack(ItemStack(Items.REDSTONE)), 1)
    )

    init {
        defaultValues()
    }

    fun defaultValues(){
        values.putAll(EnumDyeColor.values().map { Pair(SimpleStack(ItemStack(Items.DYE, 1, it.dyeDamage)), 1) }.toTypedArray())
        complexities.putAll(EnumDyeColor.values().map { Pair(SimpleStack(ItemStack(Items.DYE, 1, it.dyeDamage)), 0) }.toTypedArray())
    }

    fun registerStack(stack: ItemStack, complexity: Int){
        complexities[SimpleStack(stack)] = complexity
    }

    fun removeStack(stack: ItemStack){
        complexities.remove(SimpleStack(stack))
    }

    fun getValue(stacks: Set<ItemStack>): Int?{
        var int = Int.MAX_VALUE
        stacks.forEach {
            if (values.contains(SimpleStack(it)))
                int = Math.min(int, values[SimpleStack(it)]!!)
        }
        if (int == Int.MAX_VALUE)
            return null
        else return int
    }

    fun getComplexity(stacks: Set<ItemStack>): Int?{
        var int = Int.MAX_VALUE
        stacks.forEach {
            if (complexities.contains(SimpleStack(it)))
                int = Math.min(int, complexities[SimpleStack(it)]!!)
        }
        if (int == Int.MAX_VALUE)
            return null
        else return int
    }

    fun clear(){
        values.clear()
        complexities.clear()
        defaultValues()
    }

}