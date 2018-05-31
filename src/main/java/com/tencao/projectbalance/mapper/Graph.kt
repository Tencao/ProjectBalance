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

import com.tencao.projectbalance.ProjectBCore
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraftforge.fml.common.registry.ForgeRegistries

object Graph: Iterable<MutableMap.MutableEntry<Component, Node>> {

    override fun iterator() = graph.iterator()

    private var constructed = false

    private val graph = hashMapOf<Component, Node>()

    private val dummy by lazy {
        object : Node(ItemComponent(ItemStack(Blocks.AIR))) {
            override val value = 1
            override val complexity = 1
        }
    }

    operator fun get(component: Component): Node {
        if (!graph.containsKey(component)) {
            if (constructed) return dummy
            graph[component] = Node(component)
        }
        return graph[component]!!
    }

    operator fun get(stack: ItemStack): Node {
        return graph.entries.find { it.key.equals(stack) }?.value ?: dummy
    }

    fun clear() {
        graph.clear()
        constructed = false
    }

    fun size() = graph.size

    fun print() = ProjectBCore.LOGGER.info("Printing graph of size ${graph.size} :\n\t${graph.entries.joinToString("\n\t")}")

    private fun cleanBlocks() = graph.values.forEach(Node::cleanBlocks)

    private fun generateValues() = graph.values.forEach { it.value; it.complexity }

    fun printValues() = ProjectBCore.LOGGER.info("Printing graph of size ${graph.size} :\n\t${graph.map { "${it.key} = ${it.value.value} (${it.value.complexity})" }.joinToString("\n\t")}")

    @Suppress("UNCHECKED_CAST")
    private fun populate() {
        ForgeRegistries.RECIPES.valuesCollection/*.filter { it.recipeOutput?.item is ItemFood }*/.forEach loop@ {
            //            LogHelper.logInfo("Analyzing $it (-> ${it.recipeOutput})")
            val output: Component
            val recipe = Recipe(ItemComponent(it.recipeOutput), CraftingIngredients(it).getComponents())
            output = ItemComponent(it.recipeOutput).makeOutput()

            Graph[output].add(recipe)
        }

        FurnaceRecipes.instance().smeltingList.forEach {
            val output = ItemComponent(it.value).makeOutput()
            val recipe = FurnaceRecipe(ItemComponent(it.value), listOf(ItemComponent(it.key)))
            Graph[output].add(recipe)
        }

        ForgeRegistries.ITEMS.values.map(::ItemStack).forEach { Graph[ItemComponent(it).makeOutput()] }

        constructed = true
    }

    fun make() {
        ProjectBCore.LOGGER.info("Generating...")
        populate()
        ProjectBCore.LOGGER.info("Generated, cleaning...")
        cleanBlocks()
        ProjectBCore.LOGGER.info("Cleaned, generating values...")
        generateValues()
        ProjectBCore.LOGGER.info("Generated values!")
    }

    fun getODEntry(c: ODComponent) = ItemComponent(c.itemStacks.find { graph.containsKey(ItemComponent(it).makeOutput()) } ?: c.itemStacks[0]).makeOutput()

    private fun getNoGeneration(component: Component): NoGenerationNode {
        if (!graph.containsKey(component)) graph[component] = NoGenerationNode(component)
        return graph[component] as NoGenerationNode
    }

}
