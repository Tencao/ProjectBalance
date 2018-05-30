package com.tencao.projectbalance.mapper

open class Recipe(val output: Component, val input: List<Component>) {

    open val value: Int by lazy {
        try {
            Math.abs(input.map { it.value * it.amount }.sum()) / output.amount
        } catch (e: IllegalStateException) {
            Int.MAX_VALUE
        }
    }

    open val complexity: Int by lazy {
        try {
            input.map { it.complexity }.average().times(1.5).toInt()
        } catch (e: IllegalStateException) {
            Int.MAX_VALUE
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
    override val complexity by lazy { (super.complexity * 1.5).toInt() }
}
