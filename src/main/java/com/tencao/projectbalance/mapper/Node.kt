package com.tencao.projectbalance.mapper

open class Node(val output: Component) {

    private var visitedV = false
    private var visitedC = false

    open val value: Int by lazy {
        if (visitedV) throw IllegalStateException("Already visited $this when generating value!")
        visitedV = true
        1
    }

    open val complexity: Int by lazy {
        if (visitedC) throw IllegalStateException("Already visited $this when generating complexity!")
        visitedC = true
        1
    }

    private val recipes = mutableListOf<Recipe>()
    fun add(recipe: Recipe) = recipes.add(recipe)
    override fun toString() = "Node of $output recipes :\n\t\t${recipes.joinToString("\n\t\t")}"

    fun cleanBlocks() {
        recipes.removeAll { it.isUnBlockRecipe && Graph[it.blockForm!!].recipes.any { ot -> ot.isBlockRecipe && ot.itemForm!!.corresponds(it.itemForm) } }
    }
}

class NoGenerationNode(output: Component, override var value: Int = 1, override var complexity: Int = 1) : Node(output)
