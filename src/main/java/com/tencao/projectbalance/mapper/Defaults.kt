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

import net.minecraft.init.Items
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack

object Defaults {

    val values = hashMapOf(
            Pair(ItemComponent(ItemStack(Items.GOLD_INGOT)).makeOutput(), 1),
            Pair(ItemComponent(ItemStack(Items.GOLD_NUGGET)).makeOutput(), 1),
            Pair(ItemComponent(ItemStack(Items.REDSTONE)).makeOutput(), 1)
    )

    val complexities = hashMapOf(
            Pair(ItemComponent(ItemStack(Items.GOLD_INGOT)).makeOutput(), 1),
            Pair(ItemComponent(ItemStack(Items.REDSTONE)).makeOutput(), 1)
    )

    init {
        values.putAll(EnumDyeColor.values().map { Pair(ItemComponent(ItemStack(Items.DYE, 1, it.dyeDamage)).makeOutput(), 1) }.toTypedArray())
        complexities.putAll(EnumDyeColor.values().map { Pair(ItemComponent(ItemStack(Items.DYE, 1, it.dyeDamage)).makeOutput(), 0) }.toTypedArray())
    }

    fun registerStack(stack: ItemStack, complexity: Int){
        complexities[ItemComponent(stack.copy().splitStack(1)).makeOutput()] = complexity
        values[ItemComponent(stack.copy().splitStack(1)).makeOutput()] = complexity
    }

    fun removeStack(stack: ItemStack){
        complexities.remove(ItemComponent(stack.copy().splitStack(1)).makeOutput())
        values.remove(ItemComponent(stack.copy().splitStack(1)).makeOutput())
    }

}