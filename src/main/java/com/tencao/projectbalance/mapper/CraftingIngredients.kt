package com.tencao.projectbalance.mapper

import com.google.common.collect.Lists
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import java.util.*
import kotlin.collections.ArrayList

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
        variableInputs.forEach { components.plus(ODComponent(it.toList())) }
        return components
    }

}