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

import java.lang.IllegalStateException

open class Node(val output: Component) {

    private var visitedV = false
    private var visitedC = false

    open val value: Int by lazy {
        if (visitedV) throw IllegalStateException("Already visited $this when generating value!")
        visitedV = true
        Defaults.values[output] ?: recipes.map { it.value }.min() ?: 1
    }

    open val complexity: Int by lazy {
        if (visitedC) throw IllegalStateException("Already visited $this when generating complexity!")
        visitedC = true
        Defaults.complexities[output] ?:recipes.map { it.complexity }.min() ?: 1
    }

    private val recipes = mutableListOf<Recipe>()
    fun add(recipe: Recipe) = recipes.add(recipe)
    override fun toString() = "Node of $output recipes :\n\t\t${recipes.joinToString("\n\t\t")}"

    fun cleanBlocks() {
        recipes.removeAll { it.isUnBlockRecipe && Graph[it.blockForm!!].recipes.any { ot -> ot.isBlockRecipe && ot.itemForm!!.corresponds(it.itemForm) } }
    }
}

class NoGenerationNode(output: Component, override var value: Int = 1, override var complexity: Int = 1) : Node(output)