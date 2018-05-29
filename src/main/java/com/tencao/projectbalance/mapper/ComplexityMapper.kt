package com.tencao.projectbalance.mapper

import com.google.common.collect.Lists
import moze_intel.projecte.PECore
import moze_intel.projecte.emc.EMCMapper.emc
import moze_intel.projecte.emc.SimpleStack
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.registry.ForgeRegistries
import java.util.*

object ComplexityMapper {

    private val complexity = LinkedHashMap<SimpleStack, Int>()

    private fun setComplexity(stack: SimpleStack, outputStacks: List<SimpleStack>): Int {
        if (!complexity.containsKey(stack)) {
            PECore.LOGGER.info("Generating complexity for " + stack.toString())
            var count = 1
            if (emc.containsKey(stack))
                count += (emc[stack]!!.toFloat() / 1000f + 0.5f).toInt()
            val recipes = ForgeRegistries.RECIPES.valuesCollection.asSequence().filter { iRecipe -> iRecipe.recipeOutput.isItemEqual(stack.toItemStack()) }
            var recipeCount = 0
            for (recipe in recipes) {
                var total = 0
                var invalid = false
                for (ingredient in recipe.ingredients) {
                    if (!invalid) {
                        for (outputStack in outputStacks) {
                            if (ingredient.apply(outputStack.toItemStack())) {
                                invalid = true
                                break
                            }
                        }
                    }
                }
                if (!invalid) {
                    val stacks = getIngredientsFor(recipe)
                    for (fixedStack in stacks.first) {
                        if (!invalid) {
                            val simpleStack = SimpleStack(fixedStack)
                            if (simpleStack != stack) {
                                if (emc.containsKey(simpleStack)) {
                                    val checkList = LinkedList(outputStacks)
                                    checkList.add(stack)
                                    total += setComplexity(simpleStack, checkList)
                                } else
                                    total++
                            } else {
                                invalid = true
                                break
                            }
                        }
                    }
                    if (!invalid) {
                        for (multiStacks in stacks.second) {
                            if (!invalid) {
                                var min = 0
                                for (itemStack in multiStacks) {
                                    val simpleStack = SimpleStack(itemStack)
                                    if (simpleStack != stack) {
                                        min = if (emc.containsKey(simpleStack)) {
                                            val checkList = LinkedList(outputStacks)
                                            checkList.add(stack)
                                            if (min <= 1) {
                                                setComplexity(simpleStack, checkList)
                                            } else
                                                Math.min(min, setComplexity(SimpleStack(itemStack), checkList))
                                        } else
                                            1
                                    } else {
                                        invalid = true
                                        break
                                    }
                                }
                                total += min
                            }
                        }
                    }
                    if (!invalid) {
                        recipeCount = if (recipeCount > 0)
                            Math.min(recipeCount, total)
                        else
                            total
                    }
                }
            }
            if (recipeCount > 0)
                count *= recipeCount
            complexity[stack] = count
            return count
        } else
            return complexity[stack]!!
    }

    fun getComplexityValue(stack: SimpleStack): Int {
        return if (complexity.containsKey(stack))
            complexity[stack]!!
        else
            setComplexity(stack, Lists.newLinkedList())
    }

    fun getIngredientsFor(recipe: IRecipe): Pair<ArrayList<ItemStack>, ArrayList<Iterable<ItemStack>>> {
        val variableInputs = Lists.newArrayList<Iterable<ItemStack>>()
        val fixedInputs = Lists.newArrayList<ItemStack>()

        for (recipeItem in recipe.ingredients) {
            val matches = recipeItem.matchingStacks
            if (matches.size == 1) {
                fixedInputs.add(matches[0].copy())
            } else if (matches.isNotEmpty()) {
                val recipeItemOptions = LinkedList<ItemStack>()
                for (option in matches) {
                    recipeItemOptions.add(option.copy())
                }
                variableInputs.add(recipeItemOptions)
            }
        }

        return Pair(fixedInputs, variableInputs)
    }

}