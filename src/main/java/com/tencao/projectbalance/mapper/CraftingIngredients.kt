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

import com.google.common.collect.Lists
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import java.util.*

class CraftingIngredients(recipe: IRecipe) {

    val variableInputs: ArrayList<Iterable<ItemStack>> = Lists.newArrayList<Iterable<ItemStack>>()
    val fixedInputs: ArrayList<ItemStack> = Lists.newArrayList<ItemStack>()
    val allInputs: ArrayList<ItemStack> = Lists.newArrayList<ItemStack>()

    init {
        for (recipeItem in recipe.ingredients) {
            val matches = recipeItem.matchingStacks
            if (matches.size == 1) {
                fixedInputs.add(matches[0].copy())
                allInputs.add(matches[0].copy())
            } else if (matches.isNotEmpty()) {
                val recipeItemOptions = LinkedList<ItemStack>()
                for (option in matches) {
                    recipeItemOptions.add(option.copy())
                    allInputs.add(option.copy())
                }
                variableInputs.add(recipeItemOptions)
            }
        }
    }

    fun getCount(): Int{
        var count = Int.MAX_VALUE
        fixedInputs.asSequence().forEach { count = Math.min(count, it.count) }
        variableInputs.asSequence().forEach { it.asSequence().forEach { count = Math.min(count, it.count) }}
        if (count == Int.MAX_VALUE)
            count = 1
        return count
    }

    fun getComponents(): List<Component>{
        val components = listOf<Component>()
        fixedInputs.forEach { components.plus(ItemComponent(it)) }
        variableInputs.forEach {
            var stack: Component = ItemComponent(it.first())
            it.forEach {
                val toTest: Component = ItemComponent(it)
                if (Graph[stack].complexity > Graph[toTest].complexity)
                    stack = toTest
            }
            components.plus(stack)
        }
        variableInputs.forEach { components.plus(ODComponent(it.toList())) }
        return components
    }

}