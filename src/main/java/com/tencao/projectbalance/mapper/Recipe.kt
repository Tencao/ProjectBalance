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

open class Recipe(val output: Component, val input: List<Component>) {

    open val value: Double by lazy {
        try {
            Math.abs(input.map { it.value * it.amount }.sum()) / output.amount
        } catch (e: IllegalStateException) {
            Double.MAX_VALUE
        }
    }

    open val complexity: Double by lazy {
        try {
            input.map { it.complexity }.average().times(1.5)
        } catch (e: IllegalStateException) {
            Double.MAX_VALUE
        }
    }

    fun amountOf(component: Component) = input.filter { it.corresponds(component) }.map { it.amount }.sum()

    val isBlockRecipe by lazy {
        val c = input[0]
        output.amount == 1 && (input.size == 4 || input.size == 9) && input.all { it.amount == 1 && c.corresponds(it) }
    }

    val isUnBlockRecipe by lazy { (output.amount == 4 || output.amount == 9) && input.size == 1 && input[0].amount == 1 }

    val blockForm by lazy { if (isBlockRecipe) output.makeOutput() else if (isUnBlockRecipe) input[0].makeOutput() else null }

    val itemForm by lazy { if (isBlockRecipe) input[0].makeOutput() else if (isUnBlockRecipe) output.makeOutput() else null }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Recipe) return false

        if (output != other.output) return false
        if (input != other.input) return false

        return true
    }

    override fun hashCode(): Int {
        var result = output.hashCode()
        result = 31 * result + input.hashCode()
        return result
    }

    override fun toString(): String {
        return "Recipe(output=$output -> input=$input)"
    }
}

class FurnaceRecipe(output: Component, input: List<Component>) : Recipe(output, input) {
    override val complexity by lazy { super.complexity * 1.5 }
}
